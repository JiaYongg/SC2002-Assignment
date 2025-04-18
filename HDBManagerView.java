import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    
    public void printMenuOptions(){
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
    private void createProjectMenu() {
        // Implementation for creating a project
    }
    
    private void viewAllProjectsMenu() {
        // Implementation for viewing all projects
    }
    
    private void viewOwnProjectsMenu() {
        // Implementation for viewing own projects
    }
    
    private void editProjectMenu() {
        // Implementation for editing a project
    }
    
    private void deleteProjectMenu() {
        // Implementation for deleting a project
    }
    
    private void toggleVisibilityMenu() {
        // Implementation for toggling project visibility
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
}
