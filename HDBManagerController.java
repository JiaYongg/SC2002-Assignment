import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    private static final String PROJECT_FILE_PATH = "ProjectList.csv";

    
    /**
     * Constructor for HDBManagerController
     * 
     * @param manager The currently logged-in manager
     */
    public HDBManagerController(Manager manager) {
        this.currentManager = manager;
        this.allProjects = loadProjects();
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
    // public Project createProject(String name, String neighborhood, List<FlatType> flatTypes,
    //                            String openDateStr, String closeDateStr, int officerSlots) {
    //     try {

    //         SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    //         Date openDate = dateFormat.parse(openDateStr);
    //         Date closeDate = dateFormat.parse(closeDateStr);
            
 
    //         if (openDate.after(closeDate)) {
    //             System.out.println("Error: Opening date must be before closing date.");
    //             return null;
    //         }
            
    //         // Check if project name is unique
    //         for (Project existingProject : allProjects) {
    //             if (existingProject.getProjectName().equals(name)) {
    //                 System.out.println("Error: A project with this name already exists.");
    //                 return null;
    //             }
    //         }
            

    //         if (!canHandleProjectInPeriod(openDate, closeDate)) {
    //             System.out.println("Error: Manager already has a project during this period.");
    //             return null;
    //         }
            
    //         // Validate officer slots
    //         if (officerSlots <= 0 || officerSlots > 10) {
    //             System.out.println("Error: Officer slots must be between 1 and 10.");
    //             return null;
    //         }
            

    //         Project project = new Project(
    //             name,
    //             neighborhood,
    //             openDate,
    //             closeDate,
    //             false, // Default visibility is off
    //             flatTypes,
    //             currentManager,
    //             officerSlots
    //         );
            

    //         allProjects.add(project);
    //         currentManager.addManagedProject(project);
            

    //         saveProjects();
            
    //         System.out.println("Project created successfully: " + name);
    //         return project;
            
    //     } catch (ParseException e) {
    //         System.out.println("Error: Invalid date format. Please use dd/MM/yyyy.");
    //         return null;
    //     } catch (Exception e) {
    //         System.out.println("Error creating project: " + e.getMessage());
    //         return null;
    //     }
    // }
    

    private List<Project> loadProjects() {
        List<Project> projects = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(PROJECT_FILE_PATH))) {
            String line;
            boolean headerSkipped = false;
            
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                

                String[] data = line.split(",");
                if (data.length < 12) {
                    System.out.println("Warning: Skipping malformed line in CSV: " + line);
                    continue;
                }
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                
                String projectName = data[0];
                String neighborhood = data[1];
                

                List<FlatType> flatTypes = new ArrayList<>();
                if (!data[2].isEmpty()) {
                    FlatType type1 = new FlatType(data[2], Integer.parseInt(data[3]), Double.parseDouble(data[4]));
                    flatTypes.add(type1);
                }
                
                if (!data[5].isEmpty()) {
                    FlatType type2 = new FlatType(data[5], Integer.parseInt(data[6]), Double.parseDouble(data[7]));
                    flatTypes.add(type2);
                }
                
                // Parse dates
                Date openDate = dateFormat.parse(data[8]);
                Date closeDate = dateFormat.parse(data[9]);
                
                // Parse manager
                String managerNric = data[10];
                Manager manager = findManagerByNric(managerNric);
                
                // Parse officer slots
                int officerSlots = Integer.parseInt(data[11]);
                
                // Create project without projectID
                Project project = new Project(
                    projectName,
                    neighborhood,
                    openDate,
                    closeDate,
                    true, // Default visibility to true for loaded projects
                    flatTypes,
                    manager,
                    officerSlots
                );
                
                projects.add(project);
                
                // Add to manager's projects if it's the current manager
                if (manager != null && manager.equals(currentManager)) {
                    currentManager.addManagedProject(project);
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error reading ProjectList.csv: " + e.getMessage());
        }
        
        return projects;
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
            
            System.out.println("Project updated successfully: " + newName);
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
        
        // TODO: Check if project has applications
        // This would require access to the Application repository
        
        // Remove from lists
        allProjects.remove(project);
        currentManager.removeManagedProject(project);
        
        // Save changes
        saveProjects();
        
        System.out.println("Project deleted successfully: " + project.getProjectName());
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
        
        System.out.println("Project visibility toggled to " + (newVisibility ? "ON" : "OFF") + 
                         ": " + project.getProjectName());
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
    //     Officer officer = registration.getOfficer();
    //     project.assignOfficer(officer);
        
    //     // Update officer's handled project
    //     officer.setHandledProject(project);
        
    //     // Save changes
    //     saveProjects();
        
    //     System.out.println("Officer registration approved for: " + officer.getName());
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
        
    //     System.out.println("Officer registration rejected for: " + registration.getOfficer().getName());
    //     return true;
    // }
    
    /**
     * Approves an applicant's BTO application
     * 
     * @param application The application to approve
     * @return true if approval was successful, false otherwise
      */
    // public boolean approveApplication(Application application) {
    //     Project project = application.getProject();
        
    //     // Check if project exists and is managed by current manager
    //     if (!isProjectManagedByCurrentManager(project)) {
    //         System.out.println("Error: You can only approve applications for projects you manage.");
    //         return false;
    //     }
        
    //     // Check if there are available units for the requested flat type
    //     FlatType flatType = application.getFlatType();
    //     if (!project.hasFlatTypeAvailable(flatType)) {
    //         System.out.println("Error: No available units for flat type: " + flatType.getName());
    //         return false;
    //     }
        
    //     // Approve application
    //     application.setStatus(ApplicationStatus.SUCCESSFUL);
        
    //     // Decrease available units
    //     project.decreaseFlatTypeAvailability(flatType);
        
    //     // Save changes
    //     saveProjects();
        
    //     System.out.println("Application approved for: " + application.getApplicant().getName());
    //     return true;
    // }
    
    /**
     * Rejects an applicant's BTO application
     * 
     * @param application The application to reject
     * @return true if rejection was successful, false otherwise
     */
    // public boolean rejectApplication(Application application) {
    //     Project project = application.getProject();
        
    //     // Check if project exists and is managed by current manager
    //     if (!isProjectManagedByCurrentManager(project)) {
    //         System.out.println("Error: You can only reject applications for projects you manage.");
    //         return false;
    //     }
        
    //     // Reject application
    //     application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        
    //     // Save changes
    //     saveProjects();
        
    //     System.out.println("Application rejected for: " + application.getApplicant().getName());
    //     return true;
    // }
    
    /**
     * Approves a withdrawal request
     * 
     * @param withdrawalRequest The withdrawal request to approve
     * @return true if approval was successful, false otherwise
     */
    // public boolean approveWithdrawal(WithdrawalRequest withdrawalRequest) {
    //     Application application = withdrawalRequest.getApplication();
    //     Project project = application.getProject();
        
    //     // Check if project exists and is managed by current manager
    //     if (!isProjectManagedByCurrentManager(project)) {
    //         System.out.println("Error: You can only approve withdrawals for projects you manage.");
    //         return false;
    //     }
        
    //     // Approve withdrawal
    //     withdrawalRequest.approve();
        
    //     // If application was successful or booked, increase available units
    //     if (application.getStatus() == ApplicationStatus.SUCCESSFUL || 
    //         application.getStatus() == ApplicationStatus.BOOKED) {
    //         project.increaseFlatTypeAvailability(application.getFlatType());
    //     }
        
    //     // Save changes
    //     saveProjects();
        
    //     System.out.println("Withdrawal approved for: " + application.getApplicant().getName());
    //     return true;
    // }
    
    // /**
    //  * Rejects a withdrawal request
    //  * 
    //  * @param withdrawalRequest The withdrawal request to reject
    //  * @return true if rejection was successful, false otherwise
    //  */
    // public boolean rejectWithdrawal(WithdrawalRequest withdrawalRequest) {
    //     Application application = withdrawalRequest.getApplication();
    //     Project project = application.getProject();
        
    //     // Check if project exists and is managed by current manager
    //     if (!isProjectManagedByCurrentManager(project)) {
    //         System.out.println("Error: You can only reject withdrawals for projects you manage.");
    //         return false;
    //     }
        
    //     // Reject withdrawal
    //     withdrawalRequest.reject();
        
    //     // Save changes
    //     saveProjects();
        
    //     System.out.println("Withdrawal rejected for: " + application.getApplicant().getName());
    //     return true;
    // }
    
    /**
     * Generates a booking report with optional filters
     * 
     * @param filters Map of filter criteria (key: filter name, value: filter value)
     * @return Formatted report string
     */
    // public String generateBookingReport(Map<String, String> filters) {
    //     List<Application> allApplications = getAllBookedApplications();
    //     List<Application> filteredApplications = new ArrayList<>();
        
    //     // Apply filters
    //     for (Application app : allApplications) {
    //         if (applyFilters(app, filters)) {
    //             filteredApplications.add(app);
    //         }
    //     }
        
    //     // Generate report
    //     StringBuilder report = new StringBuilder();
    //     report.append("=== BOOKING REPORT ===\n");
    //     report.append("Total bookings: ").append(filteredApplications.size()).append("\n\n");
        
    //     // Add filters applied
    //     if (!filters.isEmpty()) {
    //         report.append("Filters applied:\n");
    //         for (Map.Entry<String, String> filter : filters.entrySet()) {
    //             report.append("- ").append(filter.getKey()).append(": ").append(filter.getValue()).append("\n");
    //         }
    //         report.append("\n");
    //     }
        
    //     // Add booking details
    //     for (int i = 0; i < filteredApplications.size(); i++) {
    //         Application app = filteredApplications.get(i);
    //         Applicant applicant = app.getApplicant();
    //         Project project = app.getProject();
    //         FlatType flatType = app.getFlatType();
            
    //         report.append(i + 1).append(". ");
    //         report.append("Applicant: ").append(applicant.getName()).append("\n");
    //         report.append("   NRIC: ").append(maskNric(applicant.getNric())).append("\n");
    //         report.append("   Age: ").append(applicant.getAge()).append("\n");
    //         report.append("   Marital Status: ").append(applicant.getMaritalStatus()).append("\n");
    //         report.append("   Project: ").append(project.getProjectName()).append("\n");
    //         report.append("   Neighborhood: ").append(project.getNeighborhood()).append("\n");
    //         report.append("   Flat Type: ").append(flatType.getName()).append("\n\n");
    //     }
        
    //     return report.toString();
    // }
    
    /**
     * Applies filters to an application
     * 
     * @param app The application to filter
     * @param filters Map of filter criteria
     * @return true if application passes all filters, false otherwise
     */
    // public boolean applyFilters(Application app, Map<String, String> filters) {
    //     if (filters == null || filters.isEmpty()) {
    //         return true; // No filters, include all applications
    //     }
        
    //     Applicant applicant = app.getApplicant();
    //     Project project = app.getProject();
    //     FlatType flatType = app.getFlatType();
        
    //     for (Map.Entry<String, String> filter : filters.entrySet()) {
    //         String key = filter.getKey().toLowerCase();
    //         String value = filter.getValue().toLowerCase();
            
    //         switch (key) {
    //             case "maritalstatus":
    //                 if (!applicant.getMaritalStatus().toLowerCase().equals(value)) {
    //                     return false;
    //                 }
    //                 break;
    //             case "flattype":
    //                 if (!flatType.getName().toLowerCase().equals(value)) {
    //                     return false;
    //                 }
    //                 break;
    //             case "neighborhood":
    //                 if (!project.getNeighborhood().toLowerCase().equals(value)) {
    //                     return false;
    //                 }
    //                 break;
    //             case "minage":
    //                 try {
    //                     int minAge = Integer.parseInt(value);
    //                     if (applicant.getAge() < minAge) {
    //                         return false;
    //                     }
    //                 } catch (NumberFormatException e) {
    //                     System.out.println("Warning: Invalid minimum age filter value: " + value);
    //                 }
    //                 break;
    //             case "maxage":
    //                 try {
    //                     int maxAge = Integer.parseInt(value);
    //                     if (applicant.getAge() > maxAge) {
    //                         return false;
    //                     }
    //                 } catch (NumberFormatException e) {
    //                     System.out.println("Warning: Invalid maximum age filter value: " + value);
    //                 }
    //                 break;
    //             case "project":
    //                 if (!project.getProjectName().toLowerCase().contains(value)) {
    //                     return false;
    //                 }
    //                 break;
    //             default:
    //                 System.out.println("Warning: Unknown filter key: " + key);
    //                 break;
    //         }
    //     }
        
    //     return true; // Passed all filters
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
    
    /**
     * Gets a list of officer registrations with the specified status
     * 
     * @param status The registration status to filter by
     * @return List of matching registration requests
     */
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
     * Gets a list of pending applications for projects managed by the current manager
     * 
     * @return List of pending applications
    //  */
    // public List<Application> getPendingApplications() {
    //     List<Application> result = new ArrayList<>();
        
    //     // This would require access to the Application repository
    //     // For now, return an empty list
        
    //     return result;
    // }
    
    /**
     * Checks if a project is managed by the current manager
     * 
     * @param project The project to check
     * @return true if project is managed by current manager, false otherwise
     */
    private boolean isProjectManagedByCurrentManager(Project project) {
        return project != null && project.getManagerInCharge().equals(currentManager);
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
    
    /**
     * Gets a list of all booked applications in the system
     * 
     * @return List of booked applications
     */
    // private List<Application> getAllBookedApplications() {
    //     List<Application> result = new ArrayList<>();
        
    //     // This would require access to the Application repository
    //     // For now, return an empty list
        
    //     return result;
    // }
    
    /**
     * Masks an NRIC for privacy
     * 
     * @param nric The NRIC to mask
     * @return Masked NRIC (e.g., S****123A)
     */
    private String maskNric(String nric) {
        if (nric == null || nric.length() < 5) {
            return nric;
        }
        return nric.charAt(0) + "****" + nric.substring(nric.length() - 3);
    }
    
    private Manager findManagerByNric(String nric) {
        // This would require access to the user repository
        // For now, if it matches the current manager's NRIC, return the current manager
        if (currentManager.getNric().equals(nric)) {
            return currentManager;
        }
        
        // Otherwise, create a placeholder manager
        Manager manager = new Manager();
        manager.setNric(nric);
        return manager;
    }
    
    
    
    /**
     * Saves projects to the CSV file
     */
    private void saveProjects() {
        // TODO: This is a temporary implementation until ProjectFileWriter is available
        System.out.println("Note: Using temporary project saving method. Will be replaced with ProjectFileWriter.");
        
        try (FileWriter writer = new FileWriter(PROJECT_FILE_PATH)) {
            // Write header
            writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1," +
                       "Type 2,Number of units for Type 2,Selling price for Type 2," +
                       "Application opening date,Application closing date,Manager,Officer Slot,Officer\n");
            
            // Write project data
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            for (Project project : allProjects) {
                StringBuilder line = new StringBuilder();
                
                // Project name and neighborhood
                line.append(project.getProjectName()).append(",");
                line.append(project.getNeighborhood()).append(",");
                
                // Flat types, units and prices
                List<FlatType> flatTypes = project.getFlatTypes();
                if (flatTypes.size() > 0) {
                    FlatType type1 = flatTypes.get(0);
                    line.append(type1.getName()).append(",");
                    line.append(type1.getUnitCount()).append(",");
                    line.append(type1.getPrice()).append(",");
                } else {
                    line.append(",,,");
                }
                
                if (flatTypes.size() > 1) {
                    FlatType type2 = flatTypes.get(1);
                    line.append(type2.getName()).append(",");
                    line.append(type2.getUnitCount()).append(",");
                    line.append(type2.getPrice()).append(",");
                } else {
                    line.append(",,,");
                }
                
                // Dates
                line.append(dateFormat.format(project.getApplicationOpenDate())).append(",");
                line.append(dateFormat.format(project.getApplicationCloseDate())).append(",");
                
                // Manager and officer slots
                line.append(project.getManagerInCharge().getNric()).append(",");
                line.append(project.getOfficerSlots()).append(",");
                
                // Officers
                if (project.getAssignedOfficers().size() > 0) {
                    StringBuilder officers = new StringBuilder();
                    for (HDBOfficer officer : project.getAssignedOfficers()) {
                        if (officers.length() > 0) {
                            officers.append(";");
                        }
                        officers.append(officer.getNric());
                    }
                    line.append(officers);
                }
                
                writer.write(line.toString() + "\n");
            }
            
            System.out.println("Projects saved successfully.");
            
        } catch (IOException e) {
            System.out.println("Error saving projects to CSV: " + e.getMessage());
        }
    }

}
