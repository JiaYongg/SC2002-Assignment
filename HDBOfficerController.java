import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class HDBOfficerController {
    private HDBOfficer currentOfficer;
    private List<Project> allProjects;
    private HDBOfficerView view;
    private static int receiptIdCounter=1;
    private static int registrationid;
    static {
        registrationid = OfficerRegistrationFileReader.getLastUsedRegistrationId("OfficerRegistration.csv") + 1;
    }
    
    public HDBOfficerController(HDBOfficer currentOfficer){
        this.currentOfficer=currentOfficer;
        loadProjectsForOfficer();
        loadRegistrationsForOfficer();
        loadApplicationsForOfficer();
    }
    
    public List<Project> getVisibleProjects() {
        List<Project> visibleProjects = new ArrayList<>();
        for (Project project : allProjects) {
            if ((project.isVisible() && currentOfficer.canRegisterForProject(project)) ||
                (currentOfficer.getAssignedProject() != null &&
                 currentOfficer.getAssignedProject().equals(project))) {
                visibleProjects.add(project);
            }
        }
        return visibleProjects;
    }
   
    public List<Project> getAvailableProjectsForRegistration() {
         List<Project> availableProjects = new ArrayList<>();
         for (Project project : allProjects) {
             if (project.getRemainingOfficerSlots() > 0 && 
                 currentOfficer.canRegisterForProject(project)) {
                 availableProjects.add(project);
             }
         }
         return availableProjects;
     }
    
    public boolean registerForProject(Project project) {
         if (!currentOfficer.canRegisterForProject(project)) {
             return false;
         }
        
         OfficerRegistration registration = new OfficerRegistration(registrationid, currentOfficer, project, OfficerRegistrationStatus.pending, new Date());
         currentOfficer.addRegistration(registration);
         project.addOfficerRegistration(registration);
         return true;
     }
    
    public List<OfficerRegistration> getRegistrations() {
        return currentOfficer.getRegistrations();
    }
    
    public Project getAssignedProject() {
        return currentOfficer.getAssignedProject();
    }
    
     public List<Enquiry> getProjectEnquiries() {
         if (currentOfficer.getAssignedProject() == null) {
             return new ArrayList<>();
         }
        
         return currentOfficer.getAssignedProject().getEnquiries();
     }
    
     public boolean replyToEnquiry(Enquiry enquiry, String reply) {
         if (currentOfficer.getAssignedProject() == null || 
             !enquiry.getProject().equals(currentOfficer.getAssignedProject())) {
             return false;
         }
        
         enquiry.setResponse(reply);
         return true;
     }
    
     public Application getApplicationByNric(String nric) {
         if (currentOfficer.getAssignedProject() == null) {
             return null;
         }
        
         // This is a simplified implementation
         // In a real system, you would search through all applications for the project
         return null;
     }
    
     public boolean updateApplicationStatus(Application app, ApplicationStatus newStatus) {
         if (currentOfficer.getAssignedProject() == null || 
             !app.getProject().equals(currentOfficer.getAssignedProject())) {
             return false;
         }
       
         app.setStatus(newStatus);
         return true;
     }
    
    
    
    public HDBOfficer getCurrentOfficer() {
        return currentOfficer;
    }
    public void registerToHandleProject(Project project) {
        HDBOfficer officer = getCurrentOfficer();
    
        
        if (project.getRegistrationByOfficer(officer) != null) {
            System.out.println("You have already registered for this project.");
            return;
        }
    
        
        if (project.getRemainingOfficerSlots() <= 0) {
            System.out.println("No officer slots available in this project.");
            return;
        }
    
        
        OfficerRegistration registration = new OfficerRegistration(registrationid, officer, project, OfficerRegistrationStatus.pending, new Date() );
    
        
        project.addOfficerRegistration(registration);
        officer.addRegistration(registration);
        OfficerRegistrationFileWriter writer = new OfficerRegistrationFileWriter();
        writer.appendRegistration(registration);
    

        System.out.println("Registration submitted for project: " + project.getProjectName());
    }
    
    
    public void applyForProject(Project project, FlatType flatType) {
        if (!project.isOpen()) {
            System.out.println("Application is closed for this project.");
            return;
        }
        if (project.equals(currentOfficer.getAssignedProject())) {
            System.out.println("You cannot apply for a project you're handling.");
            return;
        }
    

        FlatType matchedFlatType = project.getFlatTypeByName(flatType.getName());

        if (matchedFlatType == null) {
            System.out.println("Flat type not available in this project.");
            return;
        }

        System.out.println("Application submitted for " + flatType.getName() + " in " + project.getProjectName());
    }
    public void bookFlat(Application application) {
        FlatType flat = application.getFlatType();
        if (flat.getRemainingUnits() <= 0) {
            System.out.println("Booking not allowed: No remaining units.");
            return;
        }
    
        application.setStatus(ApplicationStatus.BOOKED); // Also decrements unit inside
        System.out.println("Flat booked successfully for applicant: " + application.getApplicant().getName());
    }

    public Receipt generateReceipt(Application app) {
        Receipt receipt = new Receipt( receiptIdCounter++,  app, currentOfficer);
        System.out.println("Receipt generated for: " + app.getApplicant().getName());
        return receipt;
    }
    public OfficerRegistrationStatus getRegistrationStatus(Project project) {
        OfficerRegistration reg = project.getRegistrationByOfficer(currentOfficer);
        if (reg != null) {
            return reg.getRegistrationStatus();
        }
        return OfficerRegistrationStatus.none;
    }
    public void viewHandledProjectDetails(HDBOfficer officer) {
        Project assign = officer.getAssignedProject();
        if (assign == null) {
            System.out.println("No project assigned.");
        } else {
            System.out.println("Assigned project: " + assign.getProjectName());
            view.viewAssignedProject();
        }
    }
    public void viewEnquiries(HDBOfficer officer) {
        Project assigned = officer.getAssignedProject();
        if (assigned == null) {
            System.out.println("No project assigned.");
            return;
        }

        List<Enquiry> enquiries = assigned.getEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries.");
        } else {
            view.viewProjectEnquiries();
        }
    }

    private void loadProjectsForOfficer() {
        ProjectFileReader projectReader = new ProjectFileReader();
        Map<String, Project> loadedMap = projectReader.readFromFile();  // just use locally
        this.allProjects = new ArrayList<>(loadedMap.values());
    
        // Debug output
        System.out.println("Loaded " + allProjects.size() + " total projects");
    
        // Check for any approved/assigned projects for this officer
        for (Project project : allProjects) {
            OfficerRegistration reg = project.getRegistrationByOfficer(currentOfficer);
            if (reg != null && reg.getRegistrationStatus() == OfficerRegistrationStatus.approved) {
                currentOfficer.setAssignedProject(project);
                System.out.println("Assigned project found: " + project.getProjectName());
                break;
            }
        }
    
        System.out.println("Current officer: " + currentOfficer.getName());
        System.out.println("Assigned project: " + 
            (currentOfficer.getAssignedProject() != null 
                ? currentOfficer.getAssignedProject().getProjectName()
                : "None"));
    }

    private void loadRegistrationsForOfficer() {
        ProjectFileReader projectReader = new ProjectFileReader();
        Map<String, Project> projectMap = projectReader.readFromFile();
    
        HDBOfficerFileReader officerReader = new HDBOfficerFileReader(); // must return Map<String, User>
        Map<String, User> userMap = officerReader.readFromFile();
    
        // Convert User map to HDBOfficer map
        Map<String, HDBOfficer> officerMap = new HashMap<>();
        for (User user : userMap.values()) {
            if (user instanceof HDBOfficer officer) {
                officerMap.put(officer.getNric(), officer);
            }
        }
    
        OfficerRegistrationFileReader registrationReader = new OfficerRegistrationFileReader(projectMap, officerMap);
        Map<String, OfficerRegistration> allRegs = registrationReader.readFromFile();
    
        for (OfficerRegistration reg : allRegs.values()) {
            if (reg.getOfficer().getNric().equals(currentOfficer.getNric())) {
                currentOfficer.addRegistration(reg);
            }
        }
    
        System.out.println("Loaded " + currentOfficer.getRegistrations().size() +
            " registrations for officer: " + currentOfficer.getName());
    }

    private void loadApplicationsForOfficer() {
        ProjectFileReader projectReader = new ProjectFileReader();
        Map<String, Project> projectMap = projectReader.readFromFile();
    
        // Read officers into a map of Applicants
        Map<String, Applicant> combined = new HashMap<>();
    
        // Add this officer into the applicant map since officers can apply too
        combined.put(currentOfficer.getNric(), currentOfficer);  // key = NRIC, value = HDBOfficer as Applicant
    
        // Load applications (this will automatically link them via applicant.setApplication)
        ApplicationFileReader appReader = new ApplicationFileReader(projectMap, combined);
        appReader.readFromFile();
    }

    public void submitApplicationAsOfficer(Project project, FlatType flatType) {
        // Disallow applying for a project that the officer is handling
        if (currentOfficer.getAssignedProject() != null &&
            currentOfficer.getAssignedProject().equals(project)) {
            System.out.println("You cannot apply for a project you are handling.");
            return;
        }
    
        // Disallow applying if already registered to handle the project
        for (OfficerRegistration reg : currentOfficer.getRegistrations()) {
            if (reg.getProject().equals(project)) {
                System.out.println("You cannot apply for a project you have registered to handle.");
                return;
            }
        }
    
        // Otherwise reuse applicant logic
        ApplicationController appController = new ApplicationController();
        boolean success = appController.applyForProject(currentOfficer, project, flatType);
        if (!success) {
            System.out.println("Application submission failed.");
        }
    }

    public boolean canOfficerApply(Project p) {
        if (currentOfficer.getAssignedProject() != null &&
            currentOfficer.getAssignedProject().equals(p)) return false;
    
        for (OfficerRegistration reg : currentOfficer.getRegistrations()) {
            if (reg.getProject().equals(p)) return false;
        }
    
        Application existing = currentOfficer.getApplication();
        if (existing != null && existing.getProject().equals(p)) return false;
    
        return true;
    }

    public List<Project> getEligibleProjectsToApply() {
        List<Project> eligible = new ArrayList<>();
        for (Project p : allProjects) {
            if (!p.isVisible()) continue;
            if (!canOfficerApply(p)) continue;
            for (FlatType ft : p.getFlatTypes()) {
                if (checkEligibility(currentOfficer, p, ft)) {
                    eligible.add(p);
                    break;
                }
            }
        }
        return eligible;
    }

    public boolean checkEligibility(HDBOfficer officer, Project project, FlatType flatType) {
        Date currentDate = new Date();
        if (!project.isVisible() ||
            currentDate.before(project.getApplicationOpenDate()) ||
            currentDate.after(project.getApplicationCloseDate())) {
            return false;
        }
    
        if (flatType.getUnitCount() <= 0) {
            return false;
        }
    
        // Officer can't apply for a project they are handling or have registered for
        if (officer.getAssignedProject() != null &&
            officer.getAssignedProject().getProjectName().equals(project.getProjectName())) {
            return false;
        }
    
        for (OfficerRegistration reg : officer.getRegistrations()) {
            if (reg.getProject().equals(project)) {
                return false;
            }
        }
    
        int age = officer.getAge();
        String maritalStatus = officer.getMaritalStatus();
    
        if (maritalStatus.equalsIgnoreCase("Single")) {
            return age >= 35 && flatType.getName().equalsIgnoreCase("2-Room");
        } else if (maritalStatus.equalsIgnoreCase("Married")) {
            return age >= 21 && 
                (flatType.getName().equalsIgnoreCase("2-Room") || 
                 flatType.getName().equalsIgnoreCase("3-Room"));
        }
    
        return false;
    }

    public FlatType selectFlatType(Project project) {
        List<FlatType> eligibleFlatTypes = new ArrayList<>();
        for (FlatType ft : project.getFlatTypes()) {
            if (checkEligibility(currentOfficer, project, ft)) {
                eligibleFlatTypes.add(ft);
            }
        }

        if (eligibleFlatTypes.isEmpty()) {
            System.out.println("No eligible flat types available for this project.");
            return null;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAvailable Flat Types:");
        for (int i = 0; i < eligibleFlatTypes.size(); i++) {
            System.out.println((i + 1) + ". " + eligibleFlatTypes.get(i).getName() +
                            " ($" + eligibleFlatTypes.get(i).getPrice() + ")");
        }

        System.out.print("Select flat type (1-" + eligibleFlatTypes.size() + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= eligibleFlatTypes.size()) {
                return eligibleFlatTypes.get(choice - 1);
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }

        System.out.println("Invalid selection.");
        return null;
    }
    

}       
    

