import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for HDB Manager operations in the BTO Management System.
 * Handles project management, officer registration, application approval,
 * and report generation functionalities.
 */
public class HDBManagerController {
    private Manager currentManager;
    private List<Project> allProjects;
    private ProjectFileReader projectReader;
    private ProjectFileWriter projectWriter;
    
    /**
     * Constructor for HDBManagerController
     * 
     * @param manager The currently logged-in manager
     */
    public HDBManagerController(Manager manager) {
        this.currentManager = manager;
        this.projectReader = new ProjectFileReader();
        this.projectWriter = new ProjectFileWriter();
        loadProjects();
    }
    
    /**
     * Loads projects from the CSV file using ProjectFileReader
     */
    private void loadProjects() {
        Map<String, Project> projectMap = projectReader.readFromFile();
        this.allProjects = new ArrayList<>(projectMap.values());
        
        // Associate projects with current manager
        for (Project project : allProjects) {
            if (project.getManagerInCharge() != null && 
                project.getManagerInCharge().getName().equals(currentManager.getName())) {
                currentManager.addManagedProject(project);
                System.out.println("Associated project: " + project.getProjectName() + " with manager: " + currentManager.getName());
            }
        }
        
        // Debug output
        System.out.println("Total projects loaded: " + allProjects.size());
        System.out.println("Projects managed by current manager: " + currentManager.getManagedProjects().size());
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
    
    /**
     * Creates a new BTO project with the specified details
     * 
     * @param name Project name
     * @param neighborhood Project neighborhood
     * @param flatTypes List of flat types available in the project
     * @param openDateStr Application opening date (dd/MM/yyyy)
     * @param closeDateStr Application closing date (dd/MM/yyyy)
     * @param officerSlots Number of officer slots (max 10)
     * @return The created Project object, or null if creation failed
     */
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
                officerSlots
            );
            
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
    
    /**
     * Edits an existing project with new details
     * 
     * @param project The project to edit
     * @param newName New project name
     * @param newNeighborhood New neighborhood
     * @param newFlatTypes New list of flat types
     * @param newOpenDateStr New opening date (dd/MM/yyyy)
     * @param newCloseDateStr New closing date (dd/MM/yyyy)
     * @param newOfficerSlots New number of officer slots
     * @return true if edit was successful, false otherwise
     */
    public boolean editProject(Project project, String newName, String newNeighborhood, 
                             List<FlatType> newFlatTypes, String newOpenDateStr, 
                             String newCloseDateStr, int newOfficerSlots) {
        try {
            // Check if project exists and is managed by current manager
            if (!isProjectManagedByCurrentManager(project)) {
                System.out.println("Error: You can only edit projects you manage.");
                return false;
            }
            
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date newOpenDate = dateFormat.parse(newOpenDateStr);
            Date newCloseDate = dateFormat.parse(newCloseDateStr);
            
            // Validate dates
            if (newOpenDate.after(newCloseDate)) {
                System.out.println("Error: Opening date must be before closing date.");
                return false;
            }
            
            // Check if manager can handle this project in the new period
            // (excluding the current project from the check)
            if (!canHandleProjectInPeriodExcluding(newOpenDate, newCloseDate, project)) {
                System.out.println("Error: Manager already has another project during this period.");
                return false;
            }
            
            // Validate officer slots
            if (newOfficerSlots <= 0 || newOfficerSlots > 10) {
                System.out.println("Error: Officer slots must be between 1 and 10.");
                return false;
            }
            
            // Check if reducing officer slots would remove assigned officers
            if (newOfficerSlots < project.getAssignedOfficers().size()) {
                System.out.println("Error: Cannot reduce officer slots below the number of currently assigned officers.");
                return false;
            }
            
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
    
    /**
     * Deletes a project from the system
     * 
     * @param project The project to delete
     * @return true if deletion was successful, false otherwise
     */
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
    
    /**
     * Toggles the visibility of a project
     * 
     * @param project The project to toggle visibility for
     * @return true if toggle was successful, false otherwise
     */
    public boolean toggleVisibility(Project project) {
        // Check if project exists and is managed by current manager
        if (!isProjectManagedByCurrentManager(project)) {
            System.out.println("Error: You can only toggle visibility for projects you manage.");
            return false;
        }
        
        // Toggle visibility
        boolean newVisibility = !project.isVisible();
        project.setVisibility(newVisibility);
        
        // Save changes
        saveProjects();
        
        return true;
    }
    
    /**
     * Approves an officer registration request
     * 
     * @param registration The registration request to approve
     * @return true if approval was successful, false otherwise
     */
    // public boolean approveOfficerRegistration(OfficerRegistration registration) {
    //     Project project = registration.getProject();
        
    //     // Check if project exists and is managed by current manager
    //     if (!isProjectManagedByCurrentManager(project)) {
    //         System.out.println("Error: You can only approve registrations for projects you manage.");
    //         return false;
    //     }
        
    //     // Check if there are available officer slots
    //     if (project.getRemainingOfficerSlots() <= 0) {
    //         System.out.println("Error: No remaining officer slots for this project.");
    //         return false;
    //     }
        
    //     // Approve registration
    //     registration.approve();
        
    //     // Assign officer to project
    //     HDBOfficer officer = registration.getOfficer();
    //     project.assignOfficer(officer);
        
    //     // Save changes
    //     saveProjects();
        
    //     return true;
    // }
    
    /**
     * Rejects an officer registration request
     * 
     * @param registration The registration request to reject
     * @return true if rejection was successful, false otherwise
     */
    // public boolean rejectOfficerRegistration(OfficerRegistration registration) {
    //     Project project = registration.getProject();
        
    //     // Check if project exists and is managed by current manager
    //     if (!isProjectManagedByCurrentManager(project)) {
    //         System.out.println("Error: You can only reject registrations for projects you manage.");
    //         return false;
    //     }
        
    //     // Reject registration
    //     registration.reject();
        
    //     // Save changes
    //     saveProjects();
        
    //     return true;
    // }
    
    /**
     * Gets a list of all projects in the system
     * 
     * @return List of all projects
     */
    public List<Project> viewAllProjects() {
        return new ArrayList<>(allProjects);
    }
    
    /**
     * Gets a list of projects managed by the current manager
     * 
     * @return List of managed projects
     */
    public List<Project> viewOwnProjects() {
        return currentManager.getManagedProjects();
    }
    
    // /**
    //  * Gets a list of officer registrations with the specified status
    //  * 
    //  * @param status The registration status to filter by
    //  * @return List of matching registration requests
    //  */
    // public List<OfficerRegistration> getOfficerRegistrationsByStatus(OfficerRegistrationStatus status) {
    //     List<OfficerRegistration> result = new ArrayList<>();
        
    //     for (Project project : currentManager.getManagedProjects()) {
    //         for (OfficerRegistration reg : project.getRegistrationRequests()) {
    //             if (reg.getStatus() == status) {
    //                 result.add(reg);
    //             }
    //         }
    //     }
        
    //     return result;
    // }
    
    /**
     * Checks if a project is managed by the current manager
     * 
     * @param project The project to check
     * @return true if project is managed by current manager, false otherwise
     */
    private boolean isProjectManagedByCurrentManager(Project project) {
        if (project == null || project.getManagerInCharge() == null) {
            return false;
        }
        
        // Compare by name rather than object reference
        return project.getManagerInCharge().getName().equals(currentManager.getName());
    }
    
    
    /**
     * Checks if the current manager can handle a project in the specified period
     * 
     * @param openDate Project opening date
     * @param closeDate Project closing date
     * @return true if manager can handle the project, false otherwise
     */
    private boolean canHandleProjectInPeriod(Date openDate, Date closeDate) {
        for (Project project : currentManager.getManagedProjects()) {
            if (project.isOverlappingWith(openDate, closeDate)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if the current manager can handle a project in the specified period,
     * excluding a specific project from the check
     * 
     * @param openDate Project opening date
     * @param closeDate Project closing date
     * @param excludedProject Project to exclude from the check
     * @return true if manager can handle the project, false otherwise
     */
    private boolean canHandleProjectInPeriodExcluding(Date openDate, Date closeDate, Project excludedProject) {
        for (Project project : currentManager.getManagedProjects()) {
            if (!project.equals(excludedProject) && project.isOverlappingWith(openDate, closeDate)) {
                return false;
            }
        }
        return true;
    }
}
