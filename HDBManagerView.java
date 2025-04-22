
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class HDBManagerView {
    private HDBManagerController controller;
    private ProjectController projectController;
    private Scanner scanner;

    public HDBManagerView(HDBManagerController controller) {
        this.controller = controller;
        this.projectController = new ProjectController();
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
                        viewPendingApplicationsMenu();
                        break;
                    case 9:
                        manageWithdrawalRequestsMenu();
                        break;
                    case 10:
                        // controller.generateBookingReport();
                        System.out.println("Booking report generated successfully!");
                        break;
                    case 11:
                        viewAllEnquiriesMenu();
                        break;
                    case 12:
                        viewAndReplyToMyProjectsEnquiriesMenu();
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
        System.out.println("11. View All Enquiries");
        System.out.println("12. View and Reply to My Projects' Enquiries");
        System.out.println("0. Logout");
        System.out.print("Enter your choice: ");
    }

    // Menu methods for each operation
    private void createProjectMenu() {
        // Check if current manager already has a project he's handling
        // Check if manager already has active projects
        if (!controller.canCreateNewProject()) {
            System.out.println("You already have an active project. Cannot create another.");
            System.out.println("A project is considered inactive when its application period has ended");
            System.out.println("AND it is hidden from applicants.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // If we get here, the manager can create a new project
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
            Project project = controller.createProject(name, neighborhood, flatTypes, openDateStr, closeDateStr,
                    officerSlots);
            System.out.println("Project created successfully: " + project.getProjectName());
        } catch (Exception e) {
            System.out.println("Error creating project: " + e.getMessage());
        }
    }

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
        System.out.println(
                "Assigned Officers: " + project.getAssignedOfficers().size() + "/" + project.getOfficerSlots());

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

        // Get project status from ProjectController
        ProjectController.ProjectStatus status = projectController.getProjectStatus(project);
        System.out.println("\nStatus: " + status.getStatusMessage());
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
        System.out.println("\n===== Edit Project =====");

        // Get projects managed by the current manager
        List<Project> ownProjects = controller.viewOwnProjects();

        if (ownProjects.isEmpty()) {
            System.out.println("You don't have any projects to edit.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display list of projects
        System.out.println("Your Projects:");
        System.out.println("ID | Project Name | Neighborhood | Application Period");
        System.out.println("------------------------------------------------------");

        int index = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (Project project : ownProjects) {
            String openDate = dateFormat.format(project.getApplicationOpenDate());
            String closeDate = dateFormat.format(project.getApplicationCloseDate());

            System.out.printf("%2d | %-20s | %-15s | %s to %s\n",
                    index++,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    openDate, closeDate);
        }

        // Get user selection
        System.out.print("\nEnter project ID to edit (0 to cancel): ");
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

            // Display current project details
            System.out.println("\nCurrent Project Details:");
            displayProjectDetails(selectedProject);

            // Confirm edit
            System.out.print("\nAre you sure you want to edit this project? (y/n): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                System.out.println("Edit operation canceled.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Get new values (with current values as defaults)
            System.out.println("\nEnter new values (press Enter to keep current value):");

            // Project name
            System.out.print("Project Name [" + selectedProject.getProjectName() + "]: ");
            String newName = scanner.nextLine().trim();
            if (newName.isEmpty()) {
                newName = selectedProject.getProjectName();
            }

            // Neighborhood
            System.out.print("Neighborhood [" + selectedProject.getNeighborhood() + "]: ");
            String newNeighborhood = scanner.nextLine().trim();
            if (newNeighborhood.isEmpty()) {
                newNeighborhood = selectedProject.getNeighborhood();
            }

            // Flat types
            List<FlatType> currentFlatTypes = selectedProject.getFlatTypes();
            List<FlatType> newFlatTypes = new ArrayList<>();

            // For each existing flat type, prompt for new values
            for (FlatType flatType : currentFlatTypes) {
                System.out.println("\n----- " + flatType.getName() + " Flat Type -----");

                System.out.print("Number of units [" + flatType.getUnitCount() + "]: ");
                String unitsStr = scanner.nextLine().trim();
                int units = unitsStr.isEmpty() ? flatType.getUnitCount() : Integer.parseInt(unitsStr);

                System.out.print("Base price [$" + String.format("%,.2f", flatType.getPrice()) + "]: ");
                String priceStr = scanner.nextLine().trim();
                double price = priceStr.isEmpty() ? flatType.getPrice() : Double.parseDouble(priceStr);

                newFlatTypes.add(new FlatType(flatType.getName(), units, price));
            }

            // Application dates
            System.out.print(
                    "Application opening date [" + dateFormat.format(selectedProject.getApplicationOpenDate()) + "]: ");
            String newOpenDateStr = scanner.nextLine().trim();
            if (newOpenDateStr.isEmpty()) {
                newOpenDateStr = dateFormat.format(selectedProject.getApplicationOpenDate());
            }

            System.out.print("Application closing date [" + dateFormat.format(selectedProject.getApplicationCloseDate())
                    + "]: ");
            String newCloseDateStr = scanner.nextLine().trim();
            if (newCloseDateStr.isEmpty()) {
                newCloseDateStr = dateFormat.format(selectedProject.getApplicationCloseDate());
            }

            // Officer slots
            System.out.print("Officer slots [" + selectedProject.getOfficerSlots() + "]: ");
            String officerSlotsStr = scanner.nextLine().trim();
            int newOfficerSlots = officerSlotsStr.isEmpty() ? selectedProject.getOfficerSlots()
                    : Integer.parseInt(officerSlotsStr);

            // Call controller to update project
            boolean success = controller.editProject(
                    selectedProject,
                    newName,
                    newNeighborhood,
                    newFlatTypes,
                    newOpenDateStr,
                    newCloseDateStr,
                    newOfficerSlots);

            if (success) {
                System.out.println("\nProject updated successfully!");
                System.out.println("Project: " + newName);
            } else {
                System.out.println("\nFailed to update project. Please check your inputs and try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error editing project: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void deleteProjectMenu() {
        System.out.println("\n===== Delete Project =====");
        System.out.println("Warning: This action cannot be undone!");

        // Get projects managed by the current manager
        List<Project> ownProjects = controller.viewOwnProjects();

        if (ownProjects.isEmpty()) {
            System.out.println("You don't have any projects to delete.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display list of projects
        System.out.println("Your Projects:");
        System.out.println("ID | Project Name | Neighborhood | Application Period | Officers");
        System.out.println("---------------------------------------------------------------");

        int index = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (Project project : ownProjects) {
            String openDate = dateFormat.format(project.getApplicationOpenDate());
            String closeDate = dateFormat.format(project.getApplicationCloseDate());
            int officerCount = project.getAssignedOfficers().size();

            System.out.printf("%2d | %-20s | %-15s | %s to %s | %d/%d\n",
                    index++,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    openDate, closeDate,
                    officerCount, project.getOfficerSlots());
        }

        // Get user selection
        System.out.print("\nEnter project ID to delete (0 to cancel): ");
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

            // Check if project has assigned officers
            if (!selectedProject.getAssignedOfficers().isEmpty()) {
                System.out.println("\nError: Cannot delete project with assigned officers.");
                System.out.println("You must remove all officers from the project before deleting it.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Display project details
            System.out.println("\nProject to delete:");
            System.out.println("Project Name: " + selectedProject.getProjectName());
            System.out.println("Neighborhood: " + selectedProject.getNeighborhood());
            System.out.println("Application Period: " +
                    dateFormat.format(selectedProject.getApplicationOpenDate()) + " to " +
                    dateFormat.format(selectedProject.getApplicationCloseDate()));

            // Confirm deletion with double confirmation for safety
            System.out.print("\nAre you sure you want to delete this project? (y/n): ");
            String confirmation1 = scanner.nextLine().trim().toLowerCase();

            if (!confirmation1.equals("y") && !confirmation1.equals("yes")) {
                System.out.println("Delete operation canceled.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            System.out.print("\nThis action CANNOT be undone. Type the project name to confirm deletion: ");
            String confirmation2 = scanner.nextLine().trim();

            if (!confirmation2.equals(selectedProject.getProjectName())) {
                System.out.println("Project name doesn't match. Delete operation canceled.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Call controller to delete project
            boolean success = controller.deleteProject(selectedProject);

            if (success) {
                System.out.println("\nProject deleted successfully!");
                System.out.println("Project: " + selectedProject.getProjectName());
            } else {
                System.out.println("\nFailed to delete project. Please try again later.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error deleting project: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void toggleVisibilityMenu() {
        System.out.println("\n===== Toggle Project Visibility =====");
        System.out.println(
                "As a manager, you have full control over your projects' visibility regardless of application dates.");

        // Get projects managed by the current manager
        List<Project> ownProjects = controller.viewOwnProjects();

        if (ownProjects.isEmpty()) {
            System.out.println("You don't have any projects to manage.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display list of projects with their current visibility status and dates
        System.out.println("\nYour Projects:");
        System.out.println("ID | Project Name | Neighborhood | Current Status | Application Period");
        System.out.println("-------------------------------------------------------------------------");

        int index = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();

        for (Project project : ownProjects) {
            String openDate = dateFormat.format(project.getApplicationOpenDate());
            String closeDate = dateFormat.format(project.getApplicationCloseDate());
            String visibility = project.isVisible() ? "VISIBLE" : "HIDDEN";

            // Determine if the project is currently open for applications
            String periodStatus = "";
            if (currentDate.before(project.getApplicationOpenDate())) {
                periodStatus = "(Not yet open)";
            } else if (currentDate.after(project.getApplicationCloseDate())) {
                periodStatus = "(Closed)";
            } else {
                periodStatus = "(Open)";
            }

            System.out.printf("%2d | %-20s | %-15s | %-12s | %s to %s %s\n",
                    index++,
                    project.getProjectName(),
                    project.getNeighborhood(),
                    visibility,
                    openDate, closeDate,
                    periodStatus);
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

            // Confirm the action with the user
            boolean currentVisibility = selectedProject.isVisible();
            System.out.println("\nProject: " + selectedProject.getProjectName());
            System.out.println(
                    "Current visibility: " + (currentVisibility ? "VISIBLE to applicants" : "HIDDEN from applicants"));
            System.out.print("Are you sure you want to change this to " +
                    (!currentVisibility ? "VISIBLE" : "HIDDEN") + "? (y/n): ");

            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                System.out.println("Operation canceled.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Toggle visibility
            boolean success = controller.toggleVisibility(selectedProject);

            if (success) {
                boolean newVisibility = selectedProject.isVisible();
                System.out.println("\nSuccess! Project visibility changed to: " +
                        (newVisibility ? "VISIBLE" : "HIDDEN"));

                if (newVisibility) {
                    System.out.println("The project is now visible to applicants in the system.");

                    // If project is not yet open or already closed, provide additional information
                    if (currentDate.before(selectedProject.getApplicationOpenDate())) {
                        System.out.println("Note: This project is not yet open for applications. " +
                                "Applicants will see it but cannot apply until " +
                                dateFormat.format(selectedProject.getApplicationOpenDate()) + ".");
                    } else if (currentDate.after(selectedProject.getApplicationCloseDate())) {
                        System.out.println("Note: This project's application period has ended. " +
                                "Applicants will see it but cannot apply as it closed on " +
                                dateFormat.format(selectedProject.getApplicationCloseDate()) + ".");
                    }
                } else {
                    System.out.println("The project is now hidden from applicants in the system.");
                    System.out.println(
                            "Applicants will not be able to see or apply for this project until you make it visible again.");
                }
            } else {
                System.out.println("Failed to toggle project visibility. Please try again later.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void handleOfficerRegistrationsMenu() {
        // Implementation for handling officer registrations
    }

    private void manageWithdrawalRequestsMenu() {
        System.out.println("\n===== Manage Withdrawal Requests =====");

        List<WithdrawalRequest> pendingRequests = controller.getPendingWithdrawalRequests();

        if (pendingRequests.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("Pending Withdrawal Requests:");
        System.out.println("ID | Project | Flat Type | Applicant | Date Requested");
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < pendingRequests.size(); i++) {
            WithdrawalRequest req = pendingRequests.get(i);
            Application app = req.getApplication();

            System.out.printf("%2d | %-15s | %-8s | %-15s | %s\n",
                    (i + 1),
                    app.getProject().getProjectName(),
                    app.getFlatType().getName(),
                    app.getApplicant().getName(),
                    new SimpleDateFormat("dd/MM/yyyy").format(req.getDateRequested()));
        }

        System.out.print("\nEnter request number to process (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 0 || choice > pendingRequests.size()) {
                return;
            }

            WithdrawalRequest selectedRequest = pendingRequests.get(choice - 1);
            Application app = selectedRequest.getApplication();

            System.out.println("\n===== Withdrawal Request Details =====");
            System.out.println("Request ID: " + selectedRequest.getRequestId());
            System.out.println("Project: " + app.getProject().getProjectName());
            System.out.println("Flat Type: " + app.getFlatType().getName());
            System.out
                    .println("Applicant: " + app.getApplicant().getName() + " (" + app.getApplicant().getNric() + ")");
            System.out.println(
                    "Date Requested: " + new SimpleDateFormat("dd/MM/yyyy").format(selectedRequest.getDateRequested()));
            System.out.println("Status: " + selectedRequest.getStatus());

            System.out.println("\nOptions:");
            System.out.println("1. Approve Withdrawal");
            System.out.println("2. Reject Withdrawal");
            System.out.println("0. Cancel");

            System.out.print("Enter your choice: ");
            int action = Integer.parseInt(scanner.nextLine().trim());

            switch (action) {
                case 1:
                    boolean approved = controller.approveWithdrawalRequest(selectedRequest);
                    if (approved) {
                        System.out.println("Withdrawal request approved successfully.");
                    }
                    break;
                case 2:
                    boolean rejected = controller.rejectWithdrawalRequest(selectedRequest);
                    if (rejected) {
                        System.out.println("Withdrawal request rejected successfully.");
                    }
                    break;
                default:
                    System.out.println("Operation cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    // Implement these methods in the HDBManagerView class

    private void viewAllEnquiriesMenu() {
        controller.viewAllEnquiries();
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void viewAndReplyToMyProjectsEnquiriesMenu() {
        System.out.println("\n===== Enquiries for My Projects =====");
        List<Enquiry> managerEnquiries = controller.getManagerEnquiries();

        if (managerEnquiries.isEmpty()) {
            System.out.println("No enquiries found for your projects.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        displayEnquiries(managerEnquiries, true); // Display with reply option

        System.out.println("\nOptions:");
        System.out.println("1. Reply to an Enquiry");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    replyToEnquiryMenu(managerEnquiries);
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

    private void displayEnquiries(List<Enquiry> enquiries, boolean showIndex) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("\n" + (showIndex ? "ID | " : "") + "Project | Applicant | Content | Response");
        System.out.println("----------------------------------------------------------");

        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            String content = enquiry.getContent().length() > 30 ? enquiry.getContent().substring(0, 27) + "..."
                    : enquiry.getContent();
            String response = enquiry.getResponse().isEmpty() ? "No reply yet"
                    : (enquiry.getResponse().length() > 30 ? enquiry.getResponse().substring(0, 27) + "..."
                            : enquiry.getResponse());

            if (showIndex) {
                System.out.printf("%2d | %-15s | %-15s | %-30s | %s\n",
                        (i + 1),
                        enquiry.getProject().getProjectName(),
                        enquiry.getApplicant().getName(),
                        content,
                        response);
            } else {
                System.out.printf("%-15s | %-15s | %-30s | %s\n",
                        enquiry.getProject().getProjectName(),
                        enquiry.getApplicant().getName(),
                        content,
                        response);
            }
        }
    }

    private void replyToEnquiryMenu(List<Enquiry> enquiries) {
        System.out.print("\nEnter enquiry number to reply to: ");

        try {
            int enquiryIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (enquiryIndex >= 0 && enquiryIndex < enquiries.size()) {
                Enquiry selectedEnquiry = enquiries.get(enquiryIndex);

                System.out.println("\n===== Reply to Enquiry =====");
                System.out.println("Project: " + selectedEnquiry.getProject().getProjectName());
                System.out.println("Applicant: " + selectedEnquiry.getApplicant().getName());
                System.out.println("Content: " + selectedEnquiry.getContent());
                System.out.println("Current Response: " +
                        (selectedEnquiry.getResponse().isEmpty() ? "No reply yet" : selectedEnquiry.getResponse()));

                System.out.print("\nEnter your reply: ");
                String reply = scanner.nextLine().trim();

                if (!reply.isEmpty()) {
                    boolean success = controller.replyToEnquiry(selectedEnquiry.getEnquiryID(), reply);

                    if (success) {
                        System.out.println("Reply sent successfully!");
                    } else {
                        System.out.println("Failed to send reply. Please try again later.");
                    }
                } else {
                    System.out.println("Reply cannot be empty. Operation cancelled.");
                }
            } else {
                System.out.println("Invalid enquiry number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewPendingApplicationsMenu() {
        System.out.println("\n===== Pending Applications =====");

        List<Application> pendingApplications = controller.getPendingApplications();

        if (pendingApplications.isEmpty()) {
            System.out.println("No pending applications found.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("Pending Applications:");
        System.out.println("ID | Applicant | Project | Flat Type | Date Submitted");
        System.out.println("------------------------------------------------------");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < pendingApplications.size(); i++) {
            Application app = pendingApplications.get(i);
            System.out.printf("%2d | %-15s | %-15s | %-10s | %s\n",
                    i + 1,
                    app.getApplicant().getName(),
                    app.getProject().getProjectName(),
                    app.getFlatType().getName(),
                    dateFormat.format(app.getDateApplied()));
        }

        System.out.println("\nOptions:");
        System.out.println("1. View Application Details");
        System.out.println("2. Approve Application");
        System.out.println("3. Reject Application");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    viewApplicationDetailsMenu(pendingApplications);
                    break;
                case 2:
                    approveApplicationMenu(pendingApplications);
                    break;
                case 3:
                    rejectApplicationMenu(pendingApplications);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option. Returning to main menu.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to main menu.");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void viewApplicationDetailsMenu(List<Application> pendingApplications) {
        System.out.print("\nEnter application number to view details: ");
        try {
            int appIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (appIndex >= 0 && appIndex < pendingApplications.size()) {
                Application selectedApp = pendingApplications.get(appIndex);
                displayApplicationDetails(selectedApp);
            } else {
                System.out.println("Invalid application number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void displayApplicationDetails(Application application) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("\n===== Application Details =====");
        System.out.println("Applicant: " + application.getApplicant().getName());
        System.out.println("Project: " + application.getProject().getProjectName());
        System.out.println("Flat Type: " + application.getFlatType().getName());
        System.out.println("Date Submitted: " + dateFormat.format(application.getDateApplied()));
        System.out.println("Status: " + application.getStatus());
        System.out.println("Application ID: " + application.getApplicationID());
    }

    private void approveApplicationMenu(List<Application> pendingApplications) {
        System.out.print("\nEnter application number to approve: ");
        ApplicationController applicationController = new ApplicationController();
        try {
            int appIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (appIndex >= 0 && appIndex < pendingApplications.size()) {
                Application selectedApp = pendingApplications.get(appIndex);
                boolean success = applicationController.updateApplicationStatus(selectedApp,
                        ApplicationStatus.SUCCESSFUL);
                
                
                if (success) {
                    System.out.println("Application approved successfully.");
                } else {
                    System.out.println("Failed to approve application. Please try again later.");
                }
            } else {
                System.out.println("Invalid application number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    

    private void rejectApplicationMenu(List<Application> pendingApplications) {
        System.out.print("\nEnter application number to reject: ");
        ApplicationController applicationController = new ApplicationController();
        try {
            int appIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (appIndex >= 0 && appIndex < pendingApplications.size()) {
                Application selectedApp = pendingApplications.get(appIndex);
                boolean success = applicationController.updateApplicationStatus(selectedApp,
                        ApplicationStatus.UNSUCCESSFUL);
                if (success) {
                    System.out.println("Application rejected successfully.");
                } else {
                    System.out.println("Failed to reject application. Please try again later.");
                }
            } else {
                System.out.println("Invalid application number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    public void closeScanner() {
        scanner.close();
    }

}
