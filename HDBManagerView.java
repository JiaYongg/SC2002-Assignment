import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Date;

public class HDBManagerView {
    private HDBManagerController controller;
    private Scanner scanner;

    public HDBManagerView(HDBManagerController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public boolean displayManagerMenu() {
        boolean running = true;
        while (running) {
            printMenuOptions();

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        createProjectMenu();
                        break;
                    case 2:
                        viewAllProjectsMenu();
                        break;
                    case 3:
                        viewOwnProjectsMenu();
                        break;
                    case 4:
                        editProjectMenu();
                        break;
                    case 5:
                        deleteProjectMenu();
                        break;
                    case 6:
                        toggleVisibilityMenu();
                        break;
                    case 7:
                        handleOfficerRegistrationsMenu();
                        break;
                    case 8:
                        handleApplicationsMenu();
                        break;
                    case 9:
                        handleWithdrawalsMenu();
                        break;
                    case 10:
                        // controller.generateBookingReport();
                        System.out.println("Booking report generated successfully!");
                        break;
                    case 0:
                        running = false;
                        System.out.println("Logging out...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        return true;
    }

    public void printMenuOptions() {
        System.out.println("\n===== Manager Menu =====");
        System.out.println("1. Create New Project");
        System.out.println("2. View All Projects");
        System.out.println("3. View My Projects");
        System.out.println("4. Edit Project");
        System.out.println("5. Delete Project");
        System.out.println("6. Toggle Project Visibility");
        System.out.println("7. Pending Officer Registrations");
        System.out.println("8. Pending Applications");
        System.out.println("9. Pending Withdrawal Requests");
        System.out.println("10. Generate Booking Report");
        System.out.println("0. Logout");
        System.out.print("Enter your choice: ");
    }

    // Menu methods for each operation

    private void viewAllProjectsMenu() {
        System.out.println("\n===== All BTO Projects =====");
        
        List<Project> projects = controller.viewAllProjects();
        
        if (projects.isEmpty()) {
            System.out.println("No projects found in the system.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Total projects: " + projects.size());
        System.out.println("\nID | Project Name | Neighborhood | Application Period | Visibility | Manager");
        System.out.println("-------------------------------------------------------------------------------------");
        
        int index = 1;
        for (Project project : projects) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String openDate = dateFormat.format(project.getApplicationOpenDate());
            String closeDate = dateFormat.format(project.getApplicationCloseDate());
            String visibility = project.isVisible() ? "Visible" : "Hidden";
            String managerName = project.getManagerInCharge().getName();
            
            System.out.printf("%2d | %-20s | %-15s | %s to %s | %-8s | %s\n", 
                             index++, 
                             project.getProjectName(), 
                             project.getNeighborhood(),
                             openDate, closeDate,
                             visibility,
                             managerName);
        }
        
        System.out.println("\nOptions:");
        System.out.println("1. View Project Details");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            switch (choice) {
                case 1:
                    viewProjectDetailsMenu(projects);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option. Returning to main menu.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to main menu.");
        }
    }
    
    private void viewProjectDetailsMenu(List<Project> projects) {
        System.out.print("\nEnter project number to view details: ");
        try {
            int projectIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (projectIndex >= 0 && projectIndex < projects.size()) {
                Project selectedProject = projects.get(projectIndex);
                displayProjectDetails(selectedProject);
            } else {
                System.out.println("Invalid project number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void displayProjectDetails(Project project) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        System.out.println("\n===== Project Details =====");
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Period: " + 
                         dateFormat.format(project.getApplicationOpenDate()) + " to " + 
                         dateFormat.format(project.getApplicationCloseDate()));
        System.out.println("Visibility: " + (project.isVisible() ? "Visible to applicants" : "Hidden from applicants"));
        System.out.println("Manager: " + project.getManagerInCharge().getName());
        System.out.println("Officer Slots: " + project.getOfficerSlots());
        System.out.println("Assigned Officers: " + project.getAssignedOfficers().size() + "/" + project.getOfficerSlots());
        
        // Display flat types
        System.out.println("\nFlat Types:");
        System.out.println("Type | Units | Price");
        System.out.println("------------------------");
        
        for (FlatType flatType : project.getFlatTypes()) {
            System.out.printf("%-6s | %5d | $%,.2f\n", 
                            flatType.getName(), 
                            flatType.getUnitCount(), 
                            flatType.getPrice());
        }
        
        // Check if project is currently open
        Date currentDate = new Date();
        boolean isOpen = currentDate.after(project.getApplicationOpenDate()) && 
                        currentDate.before(project.getApplicationCloseDate());
        
        System.out.println("\nStatus: " + (isOpen ? "OPEN for applications" : "CLOSED for applications"));
    }
    

    private void viewOwnProjectsMenu() {
        System.out.println("\n===== My BTO Projects =====");
        
        List<Project> projects = controller.viewOwnProjects();
        
        if (projects.isEmpty()) {
            System.out.println("You don't have any projects to manage.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Total projects: " + projects.size());
        System.out.println("\nID | Project Name | Neighborhood | Application Period | Visibility");
        System.out.println("--------------------------------------------------------------------");
        
        int index = 1;
        for (Project project : projects) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String openDate = dateFormat.format(project.getApplicationOpenDate());
            String closeDate = dateFormat.format(project.getApplicationCloseDate());
            String visibility = project.isVisible() ? "Visible" : "Hidden";
            
            System.out.printf("%2d | %-20s | %-15s | %s to %s | %s\n", 
                             index++, 
                             project.getProjectName(), 
                             project.getNeighborhood(),
                             openDate, closeDate,
                             visibility);
        }
        
        System.out.println("\nOptions:");
        System.out.println("1. View Project Details");
        System.out.println("2. Toggle Project Visibility");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            switch (choice) {
                case 1:
                    viewProjectDetailsMenu(projects);
                    break;
                case 2:
                    toggleProjectFromList(projects);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option. Returning to main menu.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to main menu.");
        }
    }
    
    // Helper method to toggle visibility from the project list
    private void toggleProjectFromList(List<Project> projects) {
        System.out.print("\nEnter project number to toggle visibility: ");
        try {
            int projectIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (projectIndex >= 0 && projectIndex < projects.size()) {
                Project selectedProject = projects.get(projectIndex);
                boolean success = controller.toggleVisibility(selectedProject);
                
                if (success) {
                    boolean newVisibility = selectedProject.isVisible();
                    System.out.println("Project visibility successfully changed to: " + 
                                     (newVisibility ? "VISIBLE" : "HIDDEN"));
                } else {
                    System.out.println("Failed to toggle project visibility.");
                }
            } else {
                System.out.println("Invalid project number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    

    private void editProjectMenu() {
        // Implementation for editing a project
    }

    private void deleteProjectMenu() {
        // Implementation for deleting a project
    }

    private void toggleVisibilityMenu() {
        System.out.println("\n===== Toggle Project Visibility =====");
        
        // Get projects managed by the current manager
        List<Project> ownProjects = controller.viewOwnProjects();
        
        if (ownProjects.isEmpty()) {
            System.out.println("You don't have any projects to manage.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display list of projects with their current visibility status
        System.out.println("Your Projects:");
        System.out.println("ID | Project Name | Neighborhood | Visibility");
        System.out.println("------------------------------------------------");
        
        int index = 1;
        for (Project project : ownProjects) {
            String visibility = project.isVisible() ? "Visible" : "Hidden";
            System.out.printf("%2d | %-20s | %-15s | %s\n", 
                             index++, 
                             project.getProjectName(), 
                             project.getNeighborhood(),
                             visibility);
        }
        
        // Get user selection
        System.out.print("\nEnter project ID to toggle visibility (0 to cancel): ");
        try {
            int projectId = Integer.parseInt(scanner.nextLine().trim());
            
            if (projectId == 0) {
                return; // User canceled
            }
            
            if (projectId < 1 || projectId > ownProjects.size()) {
                System.out.println("Invalid project ID. Please try again.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            // Get the selected project
            Project selectedProject = ownProjects.get(projectId - 1);
            
            // Toggle visibility
            boolean success = controller.toggleVisibility(selectedProject);
            
            if (success) {
                boolean newVisibility = selectedProject.isVisible();
                System.out.println("Project visibility successfully changed to: " + 
                                 (newVisibility ? "VISIBLE" : "HIDDEN"));
                System.out.println("Project: " + selectedProject.getProjectName());
                
                if (newVisibility) {
                    System.out.println("The project is now visible to applicants.");
                } else {
                    System.out.println("The project is now hidden from applicants.");
                }
            } else {
                System.out.println("Failed to toggle project visibility.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    

    private void handleOfficerRegistrationsMenu() {
        // Implementation for handling officer registrations
    }

    private void handleApplicationsMenu() {
        // Implementation for handling applications
    }

    private void handleWithdrawalsMenu() {
        // Implementation for handling withdrawals
    }

    public void closeScanner() {
        scanner.close();
    }

    private void createProjectMenu() {
        System.out.println("\n===== Create New Project =====");
    
        // Collect project information
        System.out.print("Enter project name: ");
        String name = scanner.nextLine().trim();
    
        System.out.print("Enter neighborhood: ");
        String neighborhood = scanner.nextLine().trim();
    
        // Create flat types list
        List<FlatType> flatTypes = new ArrayList<>();
    
        // Get 2-ROOM flat type details (always included)
        System.out.println("\n----- 2-ROOM Flat Type -----");
        System.out.print("Enter number of units for 2-ROOM: ");
        int units2Room = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter base price for 2-ROOM: ");
        double price2Room = Double.parseDouble(scanner.nextLine().trim());
        flatTypes.add(new FlatType("2-ROOM", units2Room, price2Room));
    
        // Get 3-ROOM flat type details (always included)
        System.out.println("\n----- 3-ROOM Flat Type -----");
        System.out.print("Enter number of units for 3-ROOM: ");
        int units3Room = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter base price for 3-ROOM: ");
        double price3Room = Double.parseDouble(scanner.nextLine().trim());
        flatTypes.add(new FlatType("3-ROOM", units3Room, price3Room));
    
        // Get dates
        System.out.print("\nEnter application opening date (dd/MM/yyyy): ");
        String openDateStr = scanner.nextLine().trim();
    
        System.out.print("Enter application closing date (dd/MM/yyyy): ");
        String closeDateStr = scanner.nextLine().trim();
    
        System.out.print("Enter number of officer slots (max 10): ");
        int officerSlots = Integer.parseInt(scanner.nextLine().trim());
        if (officerSlots > 10) {
            officerSlots = 10;
        }
    
        // Pass all collected information to the controller
        try {
            Project project = controller.createProject(name, neighborhood, flatTypes, openDateStr, closeDateStr, officerSlots);
            System.out.println("Project created successfully: " + project.getProjectName());
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
        }
    }
    

    /**
     * Parses a date string in dd/MM/yyyy format to a java.util.Date
     * 
     * @param dateStr The date string to parse
     * @return The parsed Date object
     */
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            return null;
        }
    }

}
