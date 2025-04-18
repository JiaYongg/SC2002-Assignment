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

    private void createProjectMenu() {
        System.out.println("\n===== Create New Project =====");

        // Collect project information
        System.out.print("Enter project name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter neighborhood: ");
        String neighborhood = scanner.nextLine().trim();

        // Collect flat type information
        List<FlatType> flatTypes = new ArrayList<>();

        // Get 2-ROOM flat type details
        System.out.println("Available flat types:");
        System.out.println("1. 2-ROOM");
        System.out.println("2. 3-ROOM");
        System.out.print("Enter flat types (comma-separated numbers): ");
        String[] typeChoices = scanner.nextLine().trim().split(",");

        for (String choice : typeChoices) {
            int typeNum = Integer.parseInt(choice.trim());
            FlatType selectedType = null;

            switch (typeNum) {
                case 1:
                    System.out.print("Enter number of units for 2-ROOM: ");
                    int units2Room = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter base price for 2-ROOM: ");
                    double price2Room = Double.parseDouble(scanner.nextLine().trim());
                    selectedType = new FlatType("2-ROOM", units2Room, price2Room);
                    break;
                case 2:
                    System.out.print("Enter number of units for 3-ROOM: ");
                    int units3Room = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter base price for 3-ROOM: ");
                    double price3Room = Double.parseDouble(scanner.nextLine().trim());
                    selectedType = new FlatType("3-ROOM", units3Room, price3Room);
                    break;
                default:
                    System.out.println("Invalid flat type selection: " + typeNum);
                    continue;
            }

            if (selectedType != null) {
                flatTypes.add(selectedType);
            }

            // Get dates
            System.out.print("Enter application opening date (dd/MM/yyyy): ");
            String openDateStr = scanner.nextLine().trim();

            System.out.print("Enter application closing date (dd/MM/yyyy): ");
            String closeDateStr = scanner.nextLine().trim();

            System.out.print("Enter number of officer slots (max 10): ");
            int officerSlots = Integer.parseInt(scanner.nextLine().trim());
            if (officerSlots > 10)
                officerSlots = 10;

            // Pass all collected information to the controller
            try {
                Project project = controller.createProject(name, neighborhood, flatTypes, openDateStr, closeDateStr,
                        officerSlots);
                System.out.println("Project created successfully: " + project.getProjectName());
            } catch (Exception e) {
                System.out.println("Error creating project: " + e.getMessage());
            }
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
