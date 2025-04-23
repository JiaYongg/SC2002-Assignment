import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HDBManagerController {
    private HDBManager currentManager;
    private Map<String, Project> projectMap;
    private List<Project> allProjects;
    private ProjectFileReader projectReader;
    private ProjectFileWriter projectWriter;
    private ProjectController projectController;
    private EnquiryController enquiryController;

    private WithdrawalRequestController withdrawalController;

    public HDBManagerController(HDBManager manager) {
        this.currentManager = manager;
        this.projectReader = new ProjectFileReader();
        this.projectWriter = new ProjectFileWriter();
        this.projectController = new ProjectController();

        loadProjects();
        this.enquiryController = new EnquiryController(allProjects);
        this.withdrawalController = new WithdrawalRequestController();

    }

    /**
     * Loads projects from the CSV file using ProjectFileReader
     */
    private void loadProjects() {
        this.projectMap = projectReader.readFromFile();

        // Now populate allProjects list from the map if needed
        this.allProjects = new ArrayList<>(projectMap.values());

        // Associate projects with current manager
        for (Project project : allProjects) {
            if (project.getManagerInCharge() != null &&
                    project.getManagerInCharge().getName().equals(currentManager.getName())) {
                currentManager.addManagedProject(project);
            }
        }

        // Debug output
        System.out.println("Loaded " + projectMap.size() + " total projects");
        System.out.println("Current manager: " + currentManager.getName());
        System.out.println("Manager's projects: " + currentManager.getManagedProjects().size());
    }

    /**
     * Saves projects to the CSV file using ProjectFileWriter
     */
    private void saveProjects() {
        Map<String, Project> projectMap = new HashMap<>();
        for (Project project : allProjects) {
            projectMap.put(project.getProjectName(), project);
        }
        projectWriter.writeToFile(projectMap);
    }

    public Project createProject(String name, String neighborhood, List<FlatType> flatTypes,
            String openDateStr, String closeDateStr, int officerSlots) {
        try {
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date openDate = dateFormat.parse(openDateStr);
            Date closeDate = dateFormat.parse(closeDateStr);

            // Validate dates
            if (openDate.after(closeDate)) {
                System.out.println("Error: Opening date must be before closing date.");
                return null;
            }

            // Check if project name is unique
            for (Project existingProject : allProjects) {
                if (existingProject.getProjectName().equals(name)) {
                    System.out.println("Error: A project with this name already exists.");
                    return null;
                }
            }

            // Check if manager can handle this project
            if (!canHandleProjectInPeriod(openDate, closeDate)) {
                System.out.println("Error: Manager already has a project during this period.");
                return null;
            }

            // Validate officer slots
            if (officerSlots <= 0 || officerSlots > 10) {
                System.out.println("Error: Officer slots must be between 1 and 10.");
                return null;
            }

            // Create project
            Project project = new Project(
                    name,
                    neighborhood,
                    openDate,
                    closeDate,
                    false, // Default visibility is off
                    flatTypes,
                    currentManager,
                    officerSlots);

            // Add to lists
            allProjects.add(project);
            currentManager.addManagedProject(project);

            // Save changes
            saveProjects();

            return project;

        } catch (ParseException e) {
            System.out.println("Error: Invalid date format. Please use dd/MM/yyyy.");
            return null;
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
            return null;
        }
    }

    public boolean editProject(Project project, String newName, String newNeighborhood,
            List<FlatType> newFlatTypes, String newOpenDateStr,
            String newCloseDateStr, int newOfficerSlots) {
        try {
            // ... existing validation code ...
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date newOpenDate = dateFormat.parse(newOpenDateStr);
            Date newCloseDate = dateFormat.parse(newCloseDateStr);
            // Update project details
            project.setProjectName(newName);
            project.setNeighborhood(newNeighborhood);
            project.setFlatTypes(newFlatTypes);
            project.setApplicationOpenDate(newOpenDate);
            project.setApplicationCloseDate(newCloseDate);
            project.setOfficerSlots(newOfficerSlots);

            // Save changes
            saveProjects();

            return true;

        } catch (ParseException e) {
            System.out.println("Error: Invalid date format. Please use dd/MM/yyyy.");
            return false;
        } catch (Exception e) {
            System.out.println("Error editing project: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProject(Project project) {
        // Check if project exists and is managed by current manager
        if (!isProjectManagedByCurrentManager(project)) {
            System.out.println("Error: You can only delete projects you manage.");
            return false;
        }

        // Check if project has applications or assigned officers
        if (!project.getAssignedOfficers().isEmpty()) {
            System.out.println("Error: Cannot delete project with assigned officers.");
            return false;
        }

        // Remove from lists
        allProjects.remove(project);
        currentManager.removeManagedProject(project);

        // Save changes
        saveProjects();

        return true;
    }

    public boolean toggleVisibility(Project project) {
        if (!isProjectManagedByCurrentManager(project)) {
            System.out.println("Error: You can only toggle visibility for projects you manage.");
            return false;
        }

        project.setVisibility(!project.isVisible());
        saveProjects();
        return true;
    }

    public List<Project> viewAllProjects() {
        return new ArrayList<>(allProjects);
    }

    public List<Project> viewOwnProjects() {
        return currentManager.getManagedProjects();
    }


    public List<Application> getBookedApplications(String flatTypeFilter, String maritalStatusFilter) {
        // Load user map (NRIC → User)
        ApplicantFileReader applicantReader = new ApplicantFileReader();
        Map<String, User> userMap = applicantReader.readFromFile();
    
        // Filter to actual applicants only
        Map<String, Applicant> applicantMap = new HashMap<>();
        for (User u : userMap.values()) {
            if (u instanceof Applicant) {
                applicantMap.put(u.getNric(), (Applicant) u);
            }
        }
    
        // Load project map
        ProjectFileReader projectReader = new ProjectFileReader();
        Map<String, Project> projectMap = projectReader.readFromFile();
    
        // Load applications
        ApplicationFileReader applicationReader = new ApplicationFileReader(projectMap, applicantMap);
        Map<String, Application> appMap = applicationReader.readFromFile();
    
        // Filter booked applications based on user input
        return appMap.values().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
            .filter(app -> flatTypeFilter == null || app.getFlatType().getName().equalsIgnoreCase(flatTypeFilter))
            .filter(app -> maritalStatusFilter == null || 
                app.getApplicant().getMaritalStatus().equalsIgnoreCase(maritalStatusFilter))
            .collect(Collectors.toList());
    }
    

    private boolean isProjectManagedByCurrentManager(Project project) {
        if (project == null || project.getManagerInCharge() == null) {
            return false;
        }

        // Compare by name rather than object reference
        return project.getManagerInCharge().getName().equals(currentManager.getName());
    }

    private boolean canHandleProjectInPeriod(Date openDate, Date closeDate) {
        for (Project project : currentManager.getManagedProjects()) {
            if (project.isOverlappingWith(openDate, closeDate)) {
                return false;
            }
        }
        return true;
    }

    public boolean canCreateNewProject() {
        List<Project> ownProjects = viewOwnProjects();

        if (ownProjects.isEmpty()) {
            return true; // No projects, can create a new one
        }

        // Check if any projects are still active
        for (Project project : ownProjects) {
            if (projectController.isProjectActive(project)) {
                return false; // Has an active project, cannot create a new one
            }
        }

        return true; // All projects are inactive, can create a new one
    }

    public List<WithdrawalRequest> getPendingWithdrawalsForMyProjects() {
        List<Project> myProjects = allProjects.stream()
                .filter(p -> p.getManagerInCharge() != null &&
                        p.getManagerInCharge().getName().equals(currentManager.getName()))
                .collect(Collectors.toList());

        List<WithdrawalRequest> allPending = withdrawalController.getPendingRequests();

        return allPending.stream()
                .filter(req -> myProjects.stream().anyMatch(
                        p -> p.getProjectName().equals(req.getApplication().getProject().getProjectName())))
                .collect(Collectors.toList());

    }

    public boolean approveWithdrawal(WithdrawalRequest req) {
        return withdrawalController.approveRequest(req);
    }

    public boolean rejectWithdrawal(WithdrawalRequest req) {
        return withdrawalController.rejectRequest(req);
    }

    public List<Application> getPendingApplications() {
        ApplicationController appController = new ApplicationController();
        List<Application> allPendingApplications = appController.getPendingApplications();
        List<Application> managerPendingApplications = new ArrayList<>();

        for (Application app : allPendingApplications) {
            if (isProjectManagedByCurrentManager(app.getProject())) {
                managerPendingApplications.add(app);
            }
        }

        return managerPendingApplications;
    }

    public void viewAllEnquiries() {
        enquiryController.viewAllEnquiries();
    }

    public List<Enquiry> getManagerEnquiries() {
        List<Enquiry> managerEnquiries = new ArrayList<>();

        for (Project project : currentManager.getManagedProjects()) {
            if (project.getEnquiries() != null) {
                managerEnquiries.addAll(project.getEnquiries());
            }
        }

        return managerEnquiries;
    }

    public boolean replyToEnquiry(int enquiryId, String reply) {
        // Find the project that contains this enquiry
        for (Project project : currentManager.getManagedProjects()) {
            if (project.getEnquiries() == null)
                continue;

            for (Enquiry enquiry : project.getEnquiries()) {
                if (enquiry.getEnquiryID() == enquiryId) {
                    enquiryController.replyToEnquiry(project, enquiryId, reply);
                    return true;
                }
            }
        }

        System.out.println("Error: Enquiry not found or you are not authorized to reply.");
        return false;
    }

    public HDBManager getCurrentManager() {
        return this.currentManager;
    }

    public List<OfficerRegistration> getPendingOfficerRegistrations() {
        HDBOfficerFileReader officerReader = new HDBOfficerFileReader();
        Map<String, User> userMap = officerReader.readFromFile();

        Map<String, HDBOfficer> officerMap = new HashMap<>();
        for (User u : userMap.values()) {
            if (u instanceof HDBOfficer o) {
                officerMap.put(o.getNric(), o);
            }
        }
        
        OfficerRegistrationFileReader reader = new OfficerRegistrationFileReader(projectMap, officerMap); // pass your officerMap if needed
        Map<String, OfficerRegistration> allRegistrations = reader.readFromFile();
    
        List<OfficerRegistration> pending = new ArrayList<>();
        for (OfficerRegistration reg : allRegistrations.values()) {
            Project project = reg.getProject();
            if (reg.getRegistrationStatus() == OfficerRegistrationStatus.pending &&
                project.getManagerInCharge() != null &&
                project.getManagerInCharge().getName().equals(currentManager.getName())) {
                pending.add(reg);
            }
        }
        return pending;
    }

    public boolean approveOfficerRegistration(OfficerRegistration registration) {
        Project project = registration.getProject();
        OfficerRegistrationFileWriter writer = new OfficerRegistrationFileWriter();
        if (!isProjectManagedByCurrentManager(project)) return false;
        if (project.getRemainingOfficerSlots() <= 0) return false;
    
        if (project.getRemainingOfficerSlots() <= 0) {
            registration.reject();
            writer.updateRegistration(registration);
            System.out.println("Officer slot full. Registration has been rejected.");
            return false;
        }

        registration.approve();
        project.assignOfficer(registration.getOfficer());
    
        // Persist change
        writer.updateRegistration(registration);
        saveProjects(); // if needed
        return true;
    }
    
    public boolean rejectOfficerRegistration(OfficerRegistration registration) {
        if (!isProjectManagedByCurrentManager(registration.getProject())) return false;
    
        registration.reject();
        OfficerRegistrationFileWriter writer = new OfficerRegistrationFileWriter();
        writer.updateRegistration(registration);
        return true;
    }

}
