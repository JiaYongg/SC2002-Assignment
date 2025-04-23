
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

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
                    case 1 -> createProjectMenu();
                    case 2 -> viewAllProjectsMenu();
                    case 3 -> viewOwnProjectsMenu();
                    case 4 -> editProjectMenu();
                    case 5 -> deleteProjectMenu();
                    case 6 -> toggleVisibilityMenu();
                    case 7 -> handleOfficerRegistrationsMenu();
                    case 8 -> viewPendingApplicationsMenu();
                    case 9 -> manageWithdrawalRequestsMenu();
                    case 10 -> generateBookingReport();
                    case 11 -> viewAllEnquiriesMenu();
                    case 12 -> viewAndReplyToMyProjectsEnquiriesMenu();
                    case 0 -> {
                        running = false;
                        System.out.println("Logging out...");
                    }
                    default -> System.out.println("Invalid option. Please try again.");
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

        IntStream.range(0, projects.size()).forEach(i -> {
            Project p = projects.get(i);
            printProjectSummary(p, i + 1);
        });

        System.out.println("\nOptions:");
        System.out.println("1. View Project Details");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1 -> viewProjectDetailsMenu(projects);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Returning to main menu.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to main menu.");
        }
    }

    private void printProjectSummary(Project project, int index) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String openDate = dateFormat.format(project.getApplicationOpenDate());
        String closeDate = dateFormat.format(project.getApplicationCloseDate());
        String visibility = project.isVisible() ? "Visible" : "Hidden";
        String managerName = project.getManagerInCharge().getName();

        System.out.printf("%2d | %-20s | %-15s | %s to %s | %-8s | %s\n",
                index, project.getProjectName(), project.getNeighborhood(),
                openDate, closeDate, visibility, managerName);
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

    private void printOwnProjectSummary(Project project, int index) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String openDate = dateFormat.format(project.getApplicationOpenDate());
        String closeDate = dateFormat.format(project.getApplicationCloseDate());
        String visibility = project.isVisible() ? "Visible" : "Hidden";

        System.out.printf("%2d | %-20s | %-15s | %s to %s | %s\n",
                index,
                project.getProjectName(),
                project.getNeighborhood(),
                openDate, closeDate,
                visibility);
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

        IntStream.range(0, projects.size())
                .forEach(i -> printOwnProjectSummary(projects.get(i), i + 1));

        System.out.println("\nOptions:");
        System.out.println("1. View Project Details");
        System.out.println("2. Toggle Project Visibility");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1 -> viewProjectDetailsMenu(projects);
                case 2 -> toggleProjectFromList(projects);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Returning to main menu.");
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

        List<Project> ownProjects = controller.viewOwnProjects();
        if (ownProjects.isEmpty()) {
            System.out.println("You don't have any projects to edit.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        printProjectList(ownProjects);

        Project selectedProject = selectProjectFromList(ownProjects, "edit");
        if (selectedProject == null)
            return;

        System.out.println("\nCurrent Project Details:");
        displayProjectDetails(selectedProject);

        System.out.print("\nAre you sure you want to edit this project? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Edit operation canceled.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        Project updated = gatherUpdatedProjectFields(selectedProject);
        if (updated == null)
            return;

        boolean success = controller.editProject(
                selectedProject,
                updated.getProjectName(),
                updated.getNeighborhood(),
                updated.getFlatTypes(),
                new SimpleDateFormat("dd/MM/yyyy").format(updated.getApplicationOpenDate()),
                new SimpleDateFormat("dd/MM/yyyy").format(updated.getApplicationCloseDate()),
                updated.getOfficerSlots());

        System.out.println(success
                ? "\nProject updated successfully: " + updated.getProjectName()
                : "\nFailed to update project. Please check your inputs and try again.");

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private Project selectProjectFromList(List<Project> projects, String action) {
        System.out.print("\nEnter project ID to " + action + " (0 to cancel): ");
        try {
            int projectId = Integer.parseInt(scanner.nextLine().trim());
            if (projectId == 0)
                return null;
            if (projectId < 1 || projectId > projects.size()) {
                System.out.println("Invalid project ID.");
                return null;
            }
            return projects.get(projectId - 1);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    private Project gatherUpdatedProjectFields(Project oldProject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            System.out.println("\nEnter new values (leave blank to keep current):");

            System.out.print("Project Name [" + oldProject.getProjectName() + "]: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty())
                name = oldProject.getProjectName();

            System.out.print("Neighborhood [" + oldProject.getNeighborhood() + "]: ");
            String neighborhood = scanner.nextLine().trim();
            if (neighborhood.isEmpty())
                neighborhood = oldProject.getNeighborhood();

            List<FlatType> flatTypes = new ArrayList<>();
            for (FlatType ft : oldProject.getFlatTypes()) {
                System.out.println("\n----- " + ft.getName() + " Flat Type -----");
                System.out.print("Units [" + ft.getUnitCount() + "]: ");
                String unitsStr = scanner.nextLine().trim();
                int units = unitsStr.isEmpty() ? ft.getUnitCount() : Integer.parseInt(unitsStr);

                System.out.print("Price [$" + String.format("%,.2f", ft.getPrice()) + "]: ");
                String priceStr = scanner.nextLine().trim();
                double price = priceStr.isEmpty() ? ft.getPrice() : Double.parseDouble(priceStr);

                flatTypes.add(new FlatType(ft.getName(), units, price));
            }

            System.out.print("Opening Date [" + dateFormat.format(oldProject.getApplicationOpenDate()) + "]: ");
            String openStr = scanner.nextLine().trim();
            Date open = openStr.isEmpty() ? oldProject.getApplicationOpenDate() : dateFormat.parse(openStr);

            System.out.print("Closing Date [" + dateFormat.format(oldProject.getApplicationCloseDate()) + "]: ");
            String closeStr = scanner.nextLine().trim();
            Date close = closeStr.isEmpty() ? oldProject.getApplicationCloseDate() : dateFormat.parse(closeStr);

            System.out.print("Officer Slots [" + oldProject.getOfficerSlots() + "]: ");
            String slotsStr = scanner.nextLine().trim();
            int slots = slotsStr.isEmpty() ? oldProject.getOfficerSlots() : Integer.parseInt(slotsStr);

            return new Project(name, neighborhood, open, close, oldProject.isVisible(),
                    flatTypes, oldProject.getManagerInCharge(), slots);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    private void printProjectList(List<Project> projects) {
        System.out.println("ID | Project Name | Neighborhood | Application Period");
        System.out.println("------------------------------------------------------");
        IntStream.range(0, projects.size())
                .forEach(i -> printOwnProjectSummary(projects.get(i), i + 1));
    }

    private void deleteProjectMenu() {
        System.out.println("\n===== Delete Project =====");
        System.out.println("Warning: This action cannot be undone!");
    
        List<Project> ownProjects = controller.viewOwnProjects();
        if (ownProjects.isEmpty()) {
            System.out.println("You don't have any projects to delete.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
    
        printDeletableProjects(ownProjects);
    
        Project selectedProject = selectProjectFromList(ownProjects, "delete");
        if (selectedProject == null) return;
    
        if (!selectedProject.getAssignedOfficers().isEmpty()) {
            System.out.println("\nError: Cannot delete project with assigned officers.");
            System.out.println("You must remove all officers from the project before deleting it.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
    
        if (!confirmDeletion(selectedProject)) {
            System.out.println("Delete operation canceled.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
    
        boolean success = controller.deleteProject(selectedProject);
        System.out.println(success
            ? "\nProject deleted successfully: " + selectedProject.getProjectName()
            : "\nFailed to delete project. Please try again later.");
    
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void printDeletableProjects(List<Project> projects) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
        System.out.println("ID | Project Name | Neighborhood | Application Period | Officers");
        System.out.println("---------------------------------------------------------------");
    
        IntStream.range(0, projects.size()).forEach(i -> {
            Project p = projects.get(i);
            String openDate = dateFormat.format(p.getApplicationOpenDate());
            String closeDate = dateFormat.format(p.getApplicationCloseDate());
            System.out.printf("%2d | %-20s | %-15s | %s to %s | %d/%d\n",
                    i + 1,
                    p.getProjectName(),
                    p.getNeighborhood(),
                    openDate, closeDate,
                    p.getAssignedOfficers().size(),
                    p.getOfficerSlots());
        });
    }
    private boolean confirmDeletion(Project project) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
        System.out.println("\nProject to delete:");
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Period: " +
                dateFormat.format(project.getApplicationOpenDate()) + " to " +
                dateFormat.format(project.getApplicationCloseDate()));
    
        System.out.print("\nAre you sure you want to delete this project? (y/n): ");
        String confirmation1 = scanner.nextLine().trim().toLowerCase();
        if (!confirmation1.equals("y") && !confirmation1.equals("yes")) return false;
    
        System.out.print("\nThis action CANNOT be undone. Type the project name to confirm deletion: ");
        String confirmation2 = scanner.nextLine().trim();
    
        return confirmation2.equals(project.getProjectName());
    }
    
    
    private void toggleVisibilityMenu() {
        System.out.println("\n===== Toggle Project Visibility =====");
        System.out.println("As a manager, you have full control over your projects' visibility.");
    
        List<Project> ownProjects = controller.viewOwnProjects();
        if (ownProjects.isEmpty()) {
            System.out.println("You don't have any projects to manage.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
    
        printVisibilityProjects(ownProjects);
    
        Project selectedProject = selectProjectFromList(ownProjects, "toggle visibility for");
        if (selectedProject == null) return;
    
        if (!confirmVisibilityToggle(selectedProject)) {
            System.out.println("Operation canceled.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
    
        boolean success = controller.toggleVisibility(selectedProject);
    
        if (success) {
            String newState = selectedProject.isVisible() ? "VISIBLE" : "HIDDEN";
            System.out.println("\nSuccess! Project visibility changed to: " + newState);
            showVisibilityNotes(selectedProject);
        } else {
            System.out.println("Failed to toggle project visibility. Please try again later.");
        }
    
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    private void printVisibilityProjects(List<Project> projects) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
    
        System.out.println("ID | Project Name | Neighborhood | Current Status | Application Period");
        System.out.println("-------------------------------------------------------------------------");
    
        IntStream.range(0, projects.size()).forEach(i -> {
            Project p = projects.get(i);
            String openDate = dateFormat.format(p.getApplicationOpenDate());
            String closeDate = dateFormat.format(p.getApplicationCloseDate());
            String visibility = p.isVisible() ? "VISIBLE" : "HIDDEN";
    
            String periodStatus = currentDate.before(p.getApplicationOpenDate()) ? "(Not yet open)"
                    : currentDate.after(p.getApplicationCloseDate()) ? "(Closed)" : "(Open)";
    
            System.out.printf("%2d | %-20s | %-15s | %-12s | %s to %s %s\n",
                    i + 1,
                    p.getProjectName(),
                    p.getNeighborhood(),
                    visibility,
                    openDate, closeDate,
                    periodStatus);
        });
    }
    private boolean confirmVisibilityToggle(Project project) {
        boolean currentVisibility = project.isVisible();
        System.out.println("\nProject: " + project.getProjectName());
        System.out.println("Current visibility: " + (currentVisibility ? "VISIBLE to applicants" : "HIDDEN from applicants"));
    
        System.out.print("Change this to " + (!currentVisibility ? "VISIBLE" : "HIDDEN") + "? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("y") || confirmation.equals("yes");
    }
    private void showVisibilityNotes(Project project) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date now = new Date();
    
        if (project.isVisible()) {
            if (now.before(project.getApplicationOpenDate())) {
                System.out.println("Note: This project is visible but not yet open for applications.");
                System.out.println("Applicants can view it but cannot apply until " +
                        dateFormat.format(project.getApplicationOpenDate()) + ".");
            } else if (now.after(project.getApplicationCloseDate())) {
                System.out.println("Note: This project is visible but the application period has ended.");
                System.out.println("Closed on " + dateFormat.format(project.getApplicationCloseDate()) + ".");
            }
        } else {
            System.out.println("The project is now hidden from applicants.");
        }
    }
    

    private void handleOfficerRegistrationsMenu() {
        List<OfficerRegistration> pending = controller.getPendingOfficerRegistrations();
        if (pending.isEmpty()) {
            System.out.println("No pending officer registrations.");
            return;
        }

        System.out.println("\n===== Pending Officer Registrations =====");
        for (int i = 0; i < pending.size(); i++) {
            OfficerRegistration reg = pending.get(i);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateStr = sdf.format(reg.getRegistrationDate());
            Project proj = reg.getProject();
            String fullNote = proj.getRemainingOfficerSlots() <= 0 ? " [FULL - Cannot approve]" : "";
            System.out.printf("%d) %s applied for %s on %s%s\n",
                    i + 1,
                    reg.getOfficer().getName(),
                    proj.getProjectName(),
                    dateStr,
                    fullNote);
        }

        System.out.print("Select a registration to process (0 to cancel): ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        if (choice == 0 || choice > pending.size())
            return;

        OfficerRegistration selected = pending.get(choice - 1);

        if (selected.getProject().getRemainingOfficerSlots() <= 0) {
            selected.reject(); // Automatically reject
            OfficerRegistrationFileWriter writer = new OfficerRegistrationFileWriter();
            writer.updateRegistration(selected);
            System.out.println("Registration automatically rejected due to full officer slots.");
            return;
        }

        System.out.println("1) Approve\n2) Reject");
        int action = Integer.parseInt(scanner.nextLine());

        boolean result = false;
        if (action == 1)
            result = controller.approveOfficerRegistration(selected);
        else if (action == 2)
            result = controller.rejectOfficerRegistration(selected);

        if (result) {
            String status = action == 1 ? "APPROVED" : "REJECTED";
            System.out.println("\n===== Registration " + status + " =====");
            System.out.println("Officer: " + selected.getOfficer().getName());
            System.out.println("Project: " + selected.getProject().getProjectName());
            System.out.println("New Status: " + selected.getRegistrationStatus());
        } else {
            System.out.println("\nFailed to update registration. Please try again.");
        }
    }

    public void manageWithdrawalRequestsMenu() {
        List<WithdrawalRequest> pending = controller.getPendingWithdrawalsForMyProjects();

        System.out.println("=== Pending Withdrawal Requests ===");
        if (pending.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        for (int i = 0; i < pending.size(); i++) {
            WithdrawalRequest req = pending.get(i);
            System.out.println((i + 1) + ") " + req.getApplication().getApplicant().getName() +
                    " | Project: " + req.getApplication().getProject().getProjectName() +
                    " | Flat: " + req.getApplication().getFlatType().getName() +
                    " | Requested: " + req.getDateRequested());
        }

        System.out.println("Enter number to approve/reject, or 0 to go back:");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 0 || choice > pending.size())
            return;

        WithdrawalRequest selected = pending.get(choice - 1);
        System.out.println("1) Approve\n2) Reject");
        int action = Integer.parseInt(scanner.nextLine());

        boolean result = false;
        if (action == 1) {
            result = controller.approveWithdrawal(selected);
        } else if (action == 2) {
            result = controller.rejectWithdrawal(selected);
        }

        if (result)
            System.out.println("Request updated.");
        else
            System.out.println("Action failed.");
    }

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

    public void generateBookingReport() {
        System.out.println("\n===== Generate Booking Report =====");

        System.out.print("Filter by Flat Type (leave blank for all): ");
        String flatTypeFilter = scanner.nextLine().trim();
        if (flatTypeFilter.isEmpty())
            flatTypeFilter = null;

        System.out.print("Filter by Marital Status (Single/Married, leave blank for all): ");
        String maritalStatusFilter = scanner.nextLine().trim();
        if (maritalStatusFilter.isEmpty())
            maritalStatusFilter = null;

        List<Application> bookedApps = controller.getBookedApplications(flatTypeFilter, maritalStatusFilter);

        if (bookedApps.isEmpty()) {
            System.out.println("No matching bookings found.");
            return;
        }

        System.out.println("\n--- Booking Report ---");
        System.out.printf("%-10s %-6s %-10s %-10s %-20s\n", "NRIC", "Age", "Marital", "Flat", "Project");

        for (Application app : bookedApps) {
            Applicant a = app.getApplicant();
            System.out.printf("%-10s %-6d %-10s %-10s %-20s\n",
                    a.getNric(),
                    a.getAge(),
                    a.getMaritalStatus(),
                    app.getFlatType().getName(),
                    app.getProject().getProjectName());
        }
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


