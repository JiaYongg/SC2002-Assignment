/**
 * Controller class responsible for handling application-related actions such as submitting applications,
 * updating application statuses, and retrieving pending applications for a specific project or applicant.
 */

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

    /**
     * Initializes the ApplicationController with necessary data for managing applications.
     */
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
            if (user instanceof Applicant) {
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

    /**
     * Retrieves a list of all applications that are in the pending status.
     *
     * @return List of pending applications.
     */
    public List<Application> getPendingApplications() {
        List<Application> pendingApplications = new ArrayList<>();
        for (Application app : allApplications) {
            if (app.getStatus() == ApplicationStatus.PENDING) {
                pendingApplications.add(app);
            }
        }
        return pendingApplications;
    }
    /**
     * Retrieves pending applications for a specific project.
     *
     * @param project The project whose applications need to be fetched.
     * @return List of pending applications for the project.
     */
    public List<Application> getPendingApplicationsForProject(Project project) {
        List<Application> pendingApplications = new ArrayList<>();
        for (Application app : allApplications) {
            if (app.getStatus() == ApplicationStatus.PENDING && app.getProject().equals(project)) {
                pendingApplications.add(app);
            }
        }
        return pendingApplications;
    }
    /**
     * Retrieves pending applications for a specific applicant.
     *
     * @param applicant The applicant whose pending applications are to be fetched.
     * @return List of pending applications for the applicant.
     */
    public List<Application> getPendingApplicationsForApplicant(Applicant applicant) {
        List<Application> pendingApplications = new ArrayList<>();
        for (Application app : allApplications) {
            if (app.getStatus() == ApplicationStatus.PENDING && app.getApplicant().equals(applicant)) {
                pendingApplications.add(app);
            }
        }
        return pendingApplications;
    }
    /**
     * Updates the status of a given application.
     *
     * @param application The application to update.
     * @param newStatus The new status to set for the application.
     * @return True if the status was successfully updated, false otherwise.
     */
    public boolean updateApplicationStatus(Application application, ApplicationStatus newStatus) {
        for (Application app : allApplications) {
            if (app.getApplicationID() == application.getApplicationID()) {
    
                
                if (newStatus == ApplicationStatus.SUCCESSFUL) {
                    if (app.getFlatType().getUnitCount() <= 0) {
                        System.out.println("Error: No more units available for this flat type.");
                        return false;
                    }
                }
    
                app.setStatus(newStatus);
    
                if (newStatus == ApplicationStatus.BOOKED) {
                    app.setDateBooked(new Date()); 
                }
    
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
    /**
     * Submits a new application for a project on behalf of an applicant.
     *
     * @param applicant The applicant submitting the application.
     * @param project The project the application is for.
     * @param flatType The flat type chosen by the applicant.
     * @return True if the application was successfully submitted, false otherwise.
     */
    public boolean applyForProject(Applicant applicant, Project project, FlatType flatType) {
        
        Map<String, Application> existingApps = new ApplicationFileReader(projectMap, applicantMap).readFromFile();
        
        
        if (!checkEligibility(applicant, project, flatType)) {
            System.out.println("Applicant is not eligible for this flat type in this project.");
            return false;
        }
        
        
        if (getApplicationByApplicant(applicant) != null) {
            System.out.println("Applicant already has an active application.");
            return false;
        }
        
        
        int newId = existingApps.isEmpty() ? 1000 : 
                   existingApps.keySet().stream()
                              .mapToInt(Integer::parseInt)
                              .max().getAsInt() + 1;
        
        
        Application app = new Application(newId, applicant, project, flatType);
        app.setStatus(ApplicationStatus.PENDING);
        
        
        allApplications.add(app);
        applicant.setApplication(app);
        flatType.setUnitCount(flatType.getUnitCount() - 1);
        
        
        existingApps.put(String.valueOf(newId), app);
        applicationWriter.writeToFile(existingApps);
        
        return true;
    }

    /**
     * Withdraws the application of an applicant.
     *
     * @param applicant The applicant whose application needs to be withdrawn.
     * @return True if the application was successfully withdrawn, false otherwise.
     */
    public boolean withdrawApplication(Applicant applicant) {
        Application app = getApplicationByApplicant(applicant);
        if (app == null) {
            System.out.println("No application found for this applicant.");
            return false;
        }
        
        
        if (app.getStatus() == ApplicationStatus.BOOKED) {
            System.out.println("Cannot withdraw a booked application. Please contact HDB directly.");
            return false;
        }
        
        
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
    /**
     * Retrieves the application status of a given applicant.
     *
     * @param applicant The applicant whose application status is to be retrieved.
     * @return The status of the applicant's application.
     */
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
        
        if (applicant.getApplication() != null) {
            return applicant.getApplication();
        }
        
        
        for (Application app : allApplications) {
            if (app.getApplicant().getNric().equals(applicant.getNric())) {
                
                applicant.setApplication(app);
                return app;
            }
        }
        return null;
    }

    private boolean checkEligibility(Applicant applicant, Project project, FlatType flatType) {
        
        Date currentDate = new Date();
        if (!project.isVisible() || 
            currentDate.before(project.getApplicationOpenDate()) || 
            currentDate.after(project.getApplicationCloseDate())) {
            return false;
        }
        
        
        if (flatType.getUnitCount() <= 0) {
            return false;
        }
        
        
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

    
    
    public List<Application> getAllApplications() {
        return new ArrayList<>(allApplications);
    }
    

    

    
    
    
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
