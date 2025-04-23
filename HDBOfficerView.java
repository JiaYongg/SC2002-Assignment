import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class HDBOfficerView {
    private HDBOfficerController controller;
    private HDBOfficer currentOfficer;
    private Scanner scanner;

    public HDBOfficerView(HDBOfficerController controller, HDBOfficer currentOfficer) {
        this.controller = controller;
        this.currentOfficer = currentOfficer;
        this.scanner = new Scanner(System.in);
    }

    public boolean displayOfficerMenu() {
        boolean exitProgram = false;
        boolean logout = false;

        while (!logout && !exitProgram) {
            System.out.println("\n===== HDB Officer Menu =====");
            System.out.println("1. View Available Projects");
            System.out.println("2. View Eligible Projects");
            System.out.println("3. Submit Application as Officer");
            System.out.println("4. View Applied Project");
            System.out.println("5. Register for Project");
            System.out.println("6. View Registration Status");
            System.out.println("7. View Assigned Project");
            System.out.println("8. View and Reply To Enquiries");
            System.out.println("9. Process Flat Booking");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        viewAvailableProjects();
                        break;
                    case 2:
                        displayEligibleProjects();
                        break;
                    case 3:
                        submitApplicationMenu();
                    case 4:
                        viewAppliedProject();
                        break;
                    case 5:
                        registerForProject();

                        break;
                    case 6:
                        viewRegistrationStatus();
                        break;
                    case 7:
                        viewAssignedProject();
                        break;
                    case 8:
                        viewAndReplyToProjectEnquiriesMenu();
                        break;
                    case 9:
                        processFlatBooking();
                        break;
                    case 0:
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
        List<Project> projects = controller.getVisibleProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects available.");
        } else {
            System.out.println("------------------------------------------------------");
            System.out.printf("%-5s %-20s %-15s\n", "No.", "Project Name", "Neighborhood");
            System.out.println("------------------------------------------------------");
            for (int i = 0; i < projects.size(); i++) {
                Project p = projects.get(i);
                System.out.printf("%-5d %-20s %-15s\n", i + 1, p.getProjectName(), p.getNeighborhood());
            }
            System.out.println("------------------------------------------------------");
        }
    }

    private void displayEligibleProjects() {
        System.out.println("\n===== Eligible Projects =====");
        List<Project> projects = controller.getVisibleProjects();
        boolean found = false;
        System.out.println("--------------------------------------------------------------");
        System.out.printf("%-20s %-15s %-12s %-10s\n", "Project Name", "Flat Type", "Units Left", "Price (SGD)");
        System.out.println("--------------------------------------------------------------");

        for (Project p : projects) {
            if (!controller.canOfficerApply(p))
                continue;
            for (FlatType ft : p.getFlatTypes()) {
                if (controller.checkEligibility(controller.getCurrentOfficer(), p, ft)) {
                    System.out.printf("%-20s %-15s %-12d $%-10.2f\n",
                            p.getProjectName(), ft.getName(), ft.getUnitCount(), ft.getPrice());
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("No eligible projects.");
        }
        System.out.println("--------------------------------------------------------------");
    }

    private void submitApplicationMenu() {
        System.out.println("\n===== Submit Application =====");
        List<Project> eligible = controller.getEligibleProjectsToApply();
        if (eligible.isEmpty()) {
            System.out.println("No eligible projects available.");
            return;
        }

        for (int i = 0; i < eligible.size(); i++) {
            Project p = eligible.get(i);
            System.out.println((i + 1) + ". " + p.getProjectName());
        }

        System.out.print("Select project number: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine());
            if (idx < 1 || idx > eligible.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Project selected = eligible.get(idx - 1);
            FlatType chosen = controller.selectFlatType(selected);
            if (chosen != null) {
                controller.submitApplicationAsOfficer(selected, chosen);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void registerForProject() {
        System.out.println("\n===== Register for Project =====");
        List<Project> projects = controller.getAvailableProjectsForRegistration();
        if (projects.isEmpty()) {
            System.out.println("No projects available for registration.");
        } else {
            for (int i = 0; i < projects.size(); i++) {
                Project p = projects.get(i);
                System.out.printf("%d. %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
            }

            System.out.print("Select project number to register: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= projects.size()) {
                    controller.registerToHandleProject(projects.get(choice - 1));
                } else {
                    System.out.println("Invalid project number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private void viewRegistrationStatus() {
        System.out.println("\n===== Registration Status =====");
        List<OfficerRegistration> registrations = controller.getRegistrations();

        if (registrations.isEmpty()) {
            System.out.println("You have not registered for any projects yet.");
        } else {
            System.out.println("------------------------------------------------------");
            System.out.printf("%-5s %-25s %-15s\n", "No.", "Project Name", "Status");
            System.out.println("------------------------------------------------------");
            for (int i = 0; i < registrations.size(); i++) {
                OfficerRegistration reg = registrations.get(i);
                System.out.printf("%-5d %-25s %-15s\n", i + 1, reg.getProject().getProjectName(),
                        reg.getRegistrationStatus());
            }
            System.out.println("------------------------------------------------------");
        }
    }

    public void viewAssignedProject() {
        Project assignedProject = controller.getAssignedProject(currentOfficer); // ✅ always recheck

        System.out.println("\n===== Assigned Project =====");
        if (assignedProject != null) {
            System.out.println("You are assigned to: " + assignedProject.getProjectName());
            System.out.println("Neighborhood: " + assignedProject.getNeighborhood());
            System.out.println("Application Period: " +
                    new SimpleDateFormat("dd/MM/yyyy").format(assignedProject.getApplicationOpenDate()) +
                    " to " +
                    new SimpleDateFormat("dd/MM/yyyy").format(assignedProject.getApplicationCloseDate()));
        } else {
            System.out.println("You are not assigned to any project yet.");
        }
    }

    private void viewAndReplyToProjectEnquiriesMenu() {
        System.out.println("\n===== Project Enquiries =====");
        List<Enquiry> enquiries = controller.getProjectEnquiries();

        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
            return;
        }

        // Display enquiries
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry e = enquiries.get(i);
            String content = e.getContent().length() > 30 ? e.getContent().substring(0, 27) + "..." : e.getContent();
            String response = e.getResponse().isEmpty() ? "No response yet" : e.getResponse();
            System.out.printf("%d) From: %-15s | Content: %-30s | Response: %s\n",
                    i + 1, e.getApplicant().getName(), content, response);
        }

        System.out.print("\nEnter enquiry number to reply (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0)
                return;
            if (choice < 1 || choice > enquiries.size()) {
                System.out.println("Invalid enquiry number.");
                return;
            }

            Enquiry selected = enquiries.get(choice - 1);
            System.out.println("Original Content: " + selected.getContent());
            System.out.println("Current Response: "
                    + (selected.getResponse().isEmpty() ? "No response yet" : selected.getResponse()));
            System.out.print("Enter your reply: ");
            String reply = scanner.nextLine().trim();

            if (reply.isEmpty()) {
                System.out.println("Reply cannot be empty.");
                return;
            }

            boolean success = controller.replyToEnquiry(selected.getEnquiryID(), reply);

            if (success) {
                System.out.println("Reply sent successfully.");
            } else {
                System.out.println("Failed to send reply.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void processFlatBooking() {
        System.out.println("\n===== Process Flat Booking =====");

        List<Application> eligibleApps = controller.getSuccessfulApplicationsForAssignedProject();
        if (eligibleApps.isEmpty()) {
            System.out.println("No successful applications found.");
            return;
        }

        System.out.println("Eligible Applications:");
        for (int i = 0; i < eligibleApps.size(); i++) {
            Application app = eligibleApps.get(i);
            System.out.printf("%d) %s | %s | %s\n", i + 1,
                    app.getApplicant().getName(),
                    app.getFlatType().getName(),
                    app.getApplicant().getNric());
        }

        System.out.print("Select application number to proceed with booking (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;

            if (choice < 1 || choice > eligibleApps.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Application app = eligibleApps.get(choice - 1);

            controller.bookFlat(app);
            Receipt receipt = controller.generateReceipt(app);

            System.out.println("\n===== Booking Receipt =====");
            System.out.println(receipt.formatReceipt());
            System.out.println("Booking successful for: " + app.getApplicant().getName());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    public void closeScanner() {
        scanner.close();
    }

    private void viewAppliedProject() {
        System.out.println("\n===== Applied Project Details =====");

        Application app = controller.getCurrentOfficer().getApplication();
        if (app != null) {
            System.out.println("------------------------------------------------------");
            System.out.printf("%-20s: %s\n", "Project Name", app.getProject().getProjectName());
            System.out.printf("%-20s: %s\n", "Flat Type", app.getFlatType().getName());
            System.out.printf("%-20s: %s\n", "Status", app.getStatus());
            System.out.println("------------------------------------------------------");
        } else {
            System.out.println("No project applied yet.");
        }
    }
}
