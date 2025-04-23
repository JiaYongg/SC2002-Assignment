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
    private static int receiptIdCounter = 1;
    private static int registrationid;
    private EnquiryController enquiryController;

    static {
        registrationid = OfficerRegistrationFileReader.getLastUsedRegistrationId("OfficerRegistration.csv") + 1;
    }

    public HDBOfficerController(HDBOfficer currentOfficer) {
        this.currentOfficer = currentOfficer;
        loadProjectsForOfficer();
        this.enquiryController = new EnquiryController(allProjects);
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

        OfficerRegistration registration = new OfficerRegistration(registrationid, currentOfficer, project,
                OfficerRegistrationStatus.pending, new Date());
        currentOfficer.addRegistration(registration);
        project.addOfficerRegistration(registration);
        return true;
    }

    public List<OfficerRegistration> getRegistrations() {
        return currentOfficer.getRegistrations();
    }

    public Project getAssignedProject(HDBOfficer officer) {
        Project assigned = null;

        OfficerRegistrationFileReader reader = new OfficerRegistrationFileReader(
                new ProjectFileReader().readFromFile(),
                Map.of(officer.getNric(), officer));
        Map<String, OfficerRegistration> allRegs = reader.readFromFile();

        for (OfficerRegistration reg : allRegs.values()) {
            if (reg.getOfficer().getNric().equals(officer.getNric()) &&
                    reg.getRegistrationStatus() == OfficerRegistrationStatus.approved) {
                assigned = reg.getProject();
                break;
            }
        }

        officer.setAssignedProject(assigned);
        return assigned;
    }

    public List<Enquiry> getProjectEnquiries() {
        // Step 1: Load projects
        ProjectFileReader projectReader = new ProjectFileReader();
        Map<String, Project> projectMap = projectReader.readFromFile();

        // Step 2: Load officers and registrations to get the assigned project
        OfficerRegistrationFileReader regReader = new OfficerRegistrationFileReader(projectMap,
                Map.of(currentOfficer.getNric(), currentOfficer));
        Map<String, OfficerRegistration> allRegs = regReader.readFromFile();

        Project assigned = null;
        for (OfficerRegistration reg : allRegs.values()) {
            if (reg.getOfficer().getNric().equals(currentOfficer.getNric()) &&
                    reg.getRegistrationStatus() == OfficerRegistrationStatus.approved) {
                assigned = projectMap.get(reg.getProject().getProjectName());
                break;
            }
        }

        if (assigned == null)
            return new ArrayList<>();

        // Step 3: Load applicants (needed for EnquiryFileReader)
        ApplicantFileReader applicantReader = new ApplicantFileReader();
        Map<String, User> userMap = applicantReader.readFromFile();

        Map<String, Applicant> applicantMap = new HashMap<>();
        for (User user : userMap.values()) {
            if (user instanceof Applicant a) {
                applicantMap.put(a.getNric(), a);
            }
        }

        // Step 4: Load enquiries for the project
        EnquiryFileReader enquiryReader = new EnquiryFileReader(applicantMap, projectMap);
        enquiryReader.readFromFile(); // This populates project.getEnquiries()

        return assigned.getEnquiries(); // Now properly populated
    }

    public boolean replyToEnquiry(int enquiryId, String reply) {
        Project assigned = currentOfficer.getAssignedProject();
        if (assigned == null || assigned.getEnquiries() == null) {
            System.out.println("No assigned project or enquiries.");
            return false;
        }
    
        for (Enquiry enquiry : assigned.getEnquiries()) {
            if (enquiry.getEnquiryID() == enquiryId) {
                enquiryController.replyToEnquiry(assigned, enquiryId, reply); // ✅ This saves to file
                return true;
            }
        }
    
        System.out.println("Enquiry not found.");
        return false;
    }
    
    public Application getApplicationByNric(String nric) {
        if (currentOfficer.getAssignedProject() == null)
            return null;

        Map<String, Project> projectMap = new ProjectFileReader().readFromFile();

        Map<String, User> userMap = new ApplicantFileReader().readFromFile();
        Map<String, Applicant> applicantMap = new HashMap<>();
        for (User u : userMap.values()) {
            if (u instanceof Applicant a)
                applicantMap.put(a.getNric(), a);
        }

        ApplicationFileReader reader = new ApplicationFileReader(projectMap, applicantMap);
        Map<String, Application> allApps = reader.readFromFile();

        for (Application app : allApps.values()) {
            if (app.getApplicant() != null &&
                    app.getApplicant().getNric() != null &&
                    app.getApplicant().getNric().equals(nric) &&
                    app.getProject().getProjectName().equals(currentOfficer.getAssignedProject().getProjectName()) &&
                    app.getStatus() == ApplicationStatus.SUCCESSFUL) {
                return app;
            }
        }
        return null;
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

        OfficerRegistration registration = new OfficerRegistration(registrationid, officer, project,
                OfficerRegistrationStatus.pending, new Date());

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
        Project project = application.getProject();
        FlatType bookedType = project.getFlatTypeByName(application.getFlatType().getName());

        if (bookedType == null || bookedType.getRemainingUnits() <= 0) {
            System.out.println("Booking not allowed: No remaining units.");
            return;
        }

        // Decrement the unit count for the flat type (the first time it works fine,
        // make sure it works consistently)
        bookedType.decrementUnit();
        application.setStatus(ApplicationStatus.BOOKED); // Set the booking status

        // Persist updated project list with decremented unit
        Map<String, Project> updatedProjects = new ProjectFileReader().readFromFile();

        // Ensure the updated project is saved
        updatedProjects.put(project.getProjectName(), project);

        // Write the updated project data back to the file
        new ProjectFileWriter().writeToFile(updatedProjects);

        System.out.println("Flat booked successfully for applicant: " + application.getApplicant().getName());

        // Persist booking to Application.csv
        Map<String, Project> projectMap = new ProjectFileReader().readFromFile();

        ApplicantFileReader applicantReader = new ApplicantFileReader();
        Map<String, User> userMap = applicantReader.readFromFile();

        Map<String, Applicant> applicantMap = new HashMap<>();
        for (User u : userMap.values()) {
            if (u instanceof Applicant) {
                applicantMap.put(u.getNric(), (Applicant) u);
            }
        }

        ApplicationFileReader appReader = new ApplicationFileReader(projectMap, applicantMap);
        Map<String, Application> allApps = appReader.readFromFile();
        allApps.put(String.valueOf(application.getApplicationID()), application);

        // Save the updated applications to Application.csv
        new ApplicationFileWriter().writeToFile(allApps);

        // Writing updated project data back to ProjectList.csv again (in case any
        // changes were missed)
        updatedProjects.put(application.getProject().getProjectName(), application.getProject());
        new ProjectFileWriter().writeToFile(updatedProjects);
    }

    public Receipt generateReceipt(Application app) {
        Receipt receipt = new Receipt(receiptIdCounter++, app, currentOfficer);
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

    private void loadProjectsForOfficer() {
        // Load all projects
        ProjectFileReader projectReader = new ProjectFileReader();
        Map<String, Project> projectMap = projectReader.readFromFile();
        this.allProjects = new ArrayList<>(projectMap.values());

        // Prepare a single-officer map for filtering
        Map<String, HDBOfficer> officerMap = new HashMap<>();
        officerMap.put(currentOfficer.getNric(), currentOfficer);

        // Load registrations from file
        OfficerRegistrationFileReader regReader = new OfficerRegistrationFileReader(projectMap, officerMap);
        Map<String, OfficerRegistration> allRegs = regReader.readFromFile();

        // Check if any approved registration exists
        for (OfficerRegistration reg : allRegs.values()) {
            if (reg.getOfficer().getNric().equals(currentOfficer.getNric()) &&
                    reg.getRegistrationStatus() == OfficerRegistrationStatus.approved) {
        
                // Ensure we use the project instance from allProjects (for EnquiryController compatibility)
                for (Project p : allProjects) {
                    if (p.getProjectName().equals(reg.getProject().getProjectName())) {
                        currentOfficer.setAssignedProject(p);
                        break;
                    }
                }
        
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
        combined.put(currentOfficer.getNric(), currentOfficer); // key = NRIC, value = HDBOfficer as Applicant

        // Load applications (this will automatically link them via
        // applicant.setApplication)
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
                currentOfficer.getAssignedProject().equals(p))
            return false;

        for (OfficerRegistration reg : currentOfficer.getRegistrations()) {
            if (reg.getProject().equals(p))
                return false;
        }

        Application existing = currentOfficer.getApplication();
        if (existing != null && existing.getProject().equals(p))
            return false;

        return true;
    }

    public List<Project> getEligibleProjectsToApply() {
        List<Project> eligible = new ArrayList<>();
        for (Project p : allProjects) {
            if (!p.isVisible())
                continue;
            if (!canOfficerApply(p))
                continue;
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

    public List<Application> getSuccessfulApplicationsForAssignedProject() {
        List<Application> successful = new ArrayList<>();
        if (currentOfficer.getAssignedProject() == null)
            return successful;

        // Load applications
        Project project = currentOfficer.getAssignedProject();

        // You'll need a combined applicant map again
        ApplicantFileReader reader = new ApplicantFileReader();
        Map<String, User> userMap = reader.readFromFile();
        Map<String, Applicant> applicantMap = new HashMap<>();
        for (User u : userMap.values()) {
            if (u instanceof Applicant a)
                applicantMap.put(a.getNric(), a);
        }

        ApplicationFileReader appReader = new ApplicationFileReader(
                new ProjectFileReader().readFromFile(), applicantMap);
        Map<String, Application> apps = appReader.readFromFile();

        for (Application app : apps.values()) {
            if (app.getProject().getProjectName().equals(project.getProjectName()) &&
                    app.getStatus() == ApplicationStatus.SUCCESSFUL) {
                successful.add(app);
            }
        }
        return successful;
    }
}
