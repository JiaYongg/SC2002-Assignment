import java.util.List;
import java.util.Scanner;

public class HDBOfficerView {
    private HDBOfficerController controller;
    private Scanner scanner;
    
    public HDBOfficerView(HDBOfficerController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }
    
    public boolean displayOfficerMenu() {
        boolean exitProgram = false;
        boolean logout = false;
        
        while (!logout && !exitProgram) {
            System.out.println("\n===== HDB Officer Menu =====");
            System.out.println("1. View Available Projects");
            System.out.println("2. View My Applications");
            System.out.println("3. Register for Project");
            System.out.println("4. View Registration Status");
            System.out.println("5. View Assigned Project");
            System.out.println("6. View Project Enquiries");
            System.out.println("7. Process Flat Booking");
            System.out.println("0. Logout");
            System.out.println("9. Exit Program");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        viewAvailableProjects();
                        break;
                    case 2:
                        viewMyApplications();
                        break;
                    case 3:
                        registerForProject();
                        break;
                    case 4:
                        viewRegistrationStatus();
                        break;
                    case 5:
                        viewAssignedProject();
                        break;
                    case 6:
                        viewProjectEnquiries();
                        break;
                    case 7:
                        processFlatBooking();
                        break;
                    case 0:
                        logout = true;
                        System.out.println("Logging out...");
                        break;
                    case 9:
                        exitProgram = true;
                        System.out.println("Exiting program...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        
        return exitProgram;
    }
    
    private void viewAvailableProjects() {
        System.out.println("\n===== Available Projects =====");
        System.out.println("This feature is not fully implemented in the minimal version.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void viewMyApplications() {
        System.out.println("\n===== My Applications =====");
        System.out.println("This feature is not fully implemented in the minimal version.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void registerForProject() {
        System.out.println("\n===== Register for Project =====");
        System.out.println("This feature is not fully implemented in the minimal version.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void viewRegistrationStatus() {
        System.out.println("\n===== Registration Status =====");
        List<OfficerRegistration> registrations = controller.getRegistrations();
        
        if (registrations.isEmpty()) {
            System.out.println("You have not registered for any projects yet.");
        } else {
            System.out.println("ID | Project Name | Status");
            System.out.println("-------------------------");
            
            int index = 1;
            for (OfficerRegistration reg : registrations) {
                System.out.printf("%2d | %-20s | %s\n", 
                                 index++, 
                                 reg.getProject().getProjectName(), 
                                 reg.getStatus());
            }
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void viewAssignedProject() {
        System.out.println("\n===== Assigned Project =====");
        Project assignedProject = controller.getAssignedProject();
        
        if (assignedProject == null) {
            System.out.println("You are not assigned to any project yet.");
        } else {
            System.out.println("Project Name: " + assignedProject.getProjectName());
            System.out.println("Neighborhood: " + assignedProject.getNeighborhood());
            System.out.println("Application Period: " + 
                             assignedProject.getApplicationOpenDate() + " to " + 
                             assignedProject.getApplicationCloseDate());
            
            System.out.println("\nFlat Types:");
            for (FlatType flatType : assignedProject.getFlatTypes()) {
                System.out.println(flatType.getName() + " - " + 
                                  flatType.getUnitCount() + " units available - $" + 
                                  flatType.getPrice());
            }
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void viewProjectEnquiries() {
        System.out.println("\n===== Project Enquiries =====");
        System.out.println("This feature is not fully implemented in the minimal version.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void processFlatBooking() {
        System.out.println("\n===== Process Flat Booking =====");
        System.out.println("This feature is not fully implemented in the minimal version.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    public void closeScanner() {
        scanner.close();
    }
}
