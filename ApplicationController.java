import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationController {
    private List<Application> allApplications;
    private ApplicationFileWriter applicationWriter;
    private Map<String, Project> projectMap;
    private Map<String, Applicant> applicantMap;

    public ApplicationController() {
        this.allApplications = new ArrayList<>();
        this.applicationWriter = new ApplicationFileWriter();
        loadProjects();
        loadApplicants();
        loadApplications();
    }

    private void loadProjects() {
        ProjectFileReader projectReader = new ProjectFileReader();
        this.projectMap = projectReader.readFromFile();
    }

    private void loadApplicants() {
        ApplicantFileReader applicantReader = new ApplicantFileReader();
        Map<String, User> userMap = applicantReader.readFromFile();
        this.applicantMap = new HashMap<>();
        
        for (User user : userMap.values()) {
            if (user instanceof Applicant && !(user instanceof HDBOfficer)) {
                Applicant applicant = (Applicant) user;
                applicantMap.put(applicant.getNric(), applicant);
            }
        }
    }
    private void loadApplications() {
        try {
            ApplicationFileReader reader = new ApplicationFileReader(projectMap, applicantMap);
            Map<String, Application> applicationMap = reader.readFromFile();
            this.allApplications = new ArrayList<>(applicationMap.values());
        } catch (Exception e) {
            System.out.println("Error loading applications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public List<Application> getPendingApplications() {
        List<Application> pendingApplications = new ArrayList<>();
        for (Application app : allApplications) {
            if (app.getStatus() == ApplicationStatus.PENDING) {
                pendingApplications.add(app);
            }
        }
        return pendingApplications;
    }
    public List<Application> getPendingApplicationsForProject(Project project) {
        List<Application> pendingApplications = new ArrayList<>();
        for (Application app : allApplications) {
            if (app.getStatus() == ApplicationStatus.PENDING && app.getProject().equals(project)) {
                pendingApplications.add(app);
            }
        }
        return pendingApplications;
    }
    public List<Application> getPendingApplicationsForApplicant(Applicant applicant) {
        List<Application> pendingApplications = new ArrayList<>();
        for (Application app : allApplications) {
            if (app.getStatus() == ApplicationStatus.PENDING && app.getApplicant().equals(applicant)) {
                pendingApplications.add(app);
            }
        }
        return pendingApplications;
    }
    public boolean updateApplicationStatus(Application application, ApplicationStatus newStatus) {
        // Find the application in our list
        for (Application app : allApplications) {
            if (app.getApplicationID() == application.getApplicationID()) {
                app.setStatus(newStatus);
                
                // If status is BOOKED, set the booking date
                if (newStatus == ApplicationStatus.BOOKED) {
                    app.setDateBooked(new Date());
                }
                
                // Save changes to CSV
                saveApplications();
                return true;
            }
        }
        return false;
    }


    private void saveApplications() {
        try {
            Map<String, Application> applicationMap = new HashMap<>();
            for (Application app : allApplications) {
                applicationMap.put(String.valueOf(app.getApplicationID()), app);
            }
            applicationWriter.writeToFile(applicationMap);
        } catch (Exception e) {
            System.out.println("Error saving applications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean applyForProject(Applicant applicant, Project project, FlatType flatType) {
        //Load existing applications to prevent overwrite
        Map<String, Application> existingApps = new ApplicationFileReader(projectMap, applicantMap).readFromFile();
        
        //Eligibility check (unchanged)
        if (!checkEligibility(applicant, project, flatType)) {
            System.out.println("Applicant is not eligible for this flat type in this project.");
            return false;
        }
        
        //Duplicate check (now checks both memory and file data)
        if (getApplicationByApplicant(applicant) != null) {
            System.out.println("Applicant already has an active application.");
            return false;
        }
        
        //Fixed ID generation (handles new/existing applications)
        int newId = existingApps.isEmpty() ? 1000 : 
                   existingApps.keySet().stream()
                              .mapToInt(Integer::parseInt)
                              .max().getAsInt() + 1;
        
        //Create application with generated ID
        Application app = new Application(newId, applicant, project, flatType);
        app.setStatus(ApplicationStatus.PENDING);
        
        //Memory update (unchanged)
        allApplications.add(app);
        applicant.setApplication(app);
        flatType.setUnitCount(flatType.getUnitCount() - 1);
        
        //File writing (merges new app with existing ones)
        existingApps.put(String.valueOf(newId), app);
        applicationWriter.writeToFile(existingApps);
        
        return true;
    }

    public boolean withdrawApplication(Applicant applicant) {
        Application app = getApplicationByApplicant(applicant);
        if (app == null) {
            System.out.println("No application found for this applicant.");
            return false;
        }
        
        // Check if application can be withdrawn
        if (app.getStatus() == ApplicationStatus.BOOKED) {
            System.out.println("Cannot withdraw a booked application. Please contact HDB directly.");
            return false;
        }
        
        // Create withdrawal request
        WithdrawalRequestController withdrawalController = new WithdrawalRequestController();
        boolean success = withdrawalController.createWithdrawalRequest(app);
        
        if (success) {
            System.out.println("Withdrawal request submitted. Pending approval.");
            return true;
        } else {
            System.out.println("Failed to submit withdrawal request. A pending request may already exist.");
            return false;
        }
    }

    public ApplicationStatus getApplicationStatus(Applicant applicant) {
        Application app = getApplicationByApplicant(applicant);
        if (app != null) {
            return app.getStatus();
        } else {
            System.out.println("No application found for this applicant.");
            return null;
        }
    }

    private Application getApplicationByApplicant(Applicant applicant) {
        // First check if the applicant has a cached application
        if (applicant.getApplication() != null) {
            return applicant.getApplication();
        }
        
        // Otherwise search through all applications
        for (Application app : allApplications) {
            if (app.getApplicant().getNric().equals(applicant.getNric())) {
                // Cache the application with the applicant
                applicant.setApplication(app);
                return app;
            }
        }
        return null;
    }

    private boolean checkEligibility(Applicant applicant, Project project, FlatType flatType) {
        // Check if project is visible and within application period
        Date currentDate = new Date();
        if (!project.isVisible() || 
            currentDate.before(project.getApplicationOpenDate()) || 
            currentDate.after(project.getApplicationCloseDate())) {
            return false;
        }
        
        // Check if flat type has available units
        if (flatType.getUnitCount() <= 0) {
            return false;
        }
        
        // Check applicant eligibility
        int age = applicant.getAge();
        String maritalStatus = applicant.getMaritalStatus();
        
        if (maritalStatus.equalsIgnoreCase("Single")) {
            return age >= 35 && flatType.getName().equalsIgnoreCase("2-Room");
        } else if (maritalStatus.equalsIgnoreCase("Married")) {
            return age >= 21 && (flatType.getName().equalsIgnoreCase("2-Room") || 
                                flatType.getName().equalsIgnoreCase("3-Room"));
        }
        
        return false;
    }

    // Additional methods for manager operations
    
    public List<Application> getAllApplications() {
        return new ArrayList<>(allApplications);
    }
    

    

    
    // Helper methods for WithdrawalRequestController
    
    public Map<String, Applicant> getApplicantMap() {
        return new HashMap<>(applicantMap);
    }
    
    public Map<String, Project> getProjectMap() {
        return new HashMap<>(projectMap);
    }
    
    public Map<Integer, Application> getApplicationMap() {
        Map<Integer, Application> appMap = new HashMap<>();
        for (Application app : allApplications) {
            appMap.put(app.getApplicationID(), app);
        }
        return appMap;
    }
    
    public void saveApplications(List<Application> applications) {
        this.allApplications = new ArrayList<>(applications);
        saveApplications();
    }
}
