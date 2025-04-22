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

    public HDBManagerController(HDBManager manager) {
        this.currentManager = manager;
        this.projectReader = new ProjectFileReader();
        this.projectWriter = new ProjectFileWriter();
        this.projectController = new ProjectController();

        loadProjects();
        this.enquiryController = new EnquiryController(allProjects);
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

    // public boolean approveOfficerRegistration(OfficerRegistration registration) {
    // Project project = registration.getProject();

    // // Check if project exists and is managed by current manager
    // if (!isProjectManagedByCurrentManager(project)) {
    // System.out.println("Error: You can only approve registrations for projects
    // you manage.");
    // return false;
    // }

    // // Check if there are available officer slots
    // if (project.getRemainingOfficerSlots() <= 0) {
    // System.out.println("Error: No remaining officer slots for this project.");
    // return false;
    // }

    // // Approve registration
    // registration.approve();

    // // Assign officer to project
    // HDBOfficer officer = registration.getOfficer();
    // project.assignOfficer(officer);

    // // Save changes
    // saveProjects();

    // return true;
    // }

    // public boolean rejectOfficerRegistration(OfficerRegistration registration) {
    // Project project = registration.getProject();

    // // Check if project exists and is managed by current manager
    // if (!isProjectManagedByCurrentManager(project)) {
    // System.out.println("Error: You can only reject registrations for projects you
    // manage.");
    // return false;
    // }

    // // Reject registration
    // registration.reject();

    // // Save changes
    // saveProjects();

    // return true;
    // }

    public List<Project> viewAllProjects() {
        return new ArrayList<>(allProjects);
    }

    public List<Project> viewOwnProjects() {
        return currentManager.getManagedProjects();
    }

    // public List<OfficerRegistration>
    // getOfficerRegistrationsByStatus(OfficerRegistrationStatus status) {
    // List<OfficerRegistration> result = new ArrayList<>();

    // for (Project project : currentManager.getManagedProjects()) {
    // for (OfficerRegistration reg : project.getRegistrationRequests()) {
    // if (reg.getStatus() == status) {
    // result.add(reg);
    // }
    // }
    // }

    // return result;
    // }

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

    public List<WithdrawalRequest> getPendingWithdrawalRequests() {
        WithdrawalRequestController withdrawalController = new WithdrawalRequestController();

        // Get all pending requests and filter for current manager's projects
        return withdrawalController.getPendingRequests().stream()
                .filter(req -> isProjectManagedByCurrentManager(req.getApplication().getProject()))
                .collect(Collectors.toList());
    }

    public boolean approveWithdrawalRequest(WithdrawalRequest request) {
        // Check if manager is authorized
        if (!isProjectManagedByCurrentManager(request.getApplication().getProject())) {
            System.out.println("Error: You are not authorized to approve this withdrawal request.");
            return false;
        }

        WithdrawalRequestController withdrawalController = new WithdrawalRequestController();
        return withdrawalController.approveRequest(request);
    }

    public boolean rejectWithdrawalRequest(WithdrawalRequest request) {
        // Check if manager is authorized
        if (!isProjectManagedByCurrentManager(request.getApplication().getProject())) {
            System.out.println("Error: You are not authorized to reject this withdrawal request.");
            return false;
        }

        WithdrawalRequestController withdrawalController = new WithdrawalRequestController();
        return withdrawalController.rejectRequest(request);
    }

    public List<Application> getPendingApplications() {
        ApplicationController appController = new ApplicationController();
        return appController.getPendingApplications();
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

}
