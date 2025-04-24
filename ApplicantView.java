import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplicantView {
    private Scanner scanner;
    private ApplicantController controller;

    public ApplicantView(ApplicantController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public boolean displayApplicantMenu() {
        boolean logout = false;

        while (!logout) {
            System.out.println("\n===== Applicant Menu =====");
            System.out.println("1. View Eligible Projects");
            System.out.println("2. Submit Application");
            System.out.println("3. View Application Status");
            System.out.println("4. Withdraw Application");
            System.out.println("5. Submit Enquiry");
            System.out.println("6. View My Enquiries");
            System.out.println("7. Edit Enquiry");
            System.out.println("8. Delete Enquiry");
            System.out.println("0. Logout");

            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        displayEligibleProjects();
                        break;
                    case 2:
                        submitApplicationMenu();
                        break;
                    case 3:
                        viewAppliedProject(controller.getApplicant());
                        break;
                    case 4:
                        controller.withdraw(controller.getApplicant().getApplication());
                        break;
                    case 5:
                        submitEnquiry();
                        break;
                    case 6:
                        viewEnquiries();
                        break;
                    case 7:
                        editEnquiry();
                        break;
                    case 8:
                        deleteEnquiry();
                        break;
                    case 0:
                        logout = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return logout;
    }

    public void displayEligibleProjects() {
        List<Project> eligibleProjects = controller.getEligibleProjects(controller.getApplicant());
        
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects found.");
            return;
        }
        
        System.out.println("\n===== Eligible Projects =====");
        System.out.println("------------------------------------------------------");
        System.out.printf("%-20s %-15s %-15s %-10s\n", "Project Name", "Flat Type", "Units Left", "Price (SGD)");
        System.out.println("------------------------------------------------------");
        
        for (Project project : eligibleProjects) {
            boolean hasEligibleFlatTypes = false;
            
            for (FlatType flatType : project.getFlatTypes()) {
                if (flatType.getUnitCount() > 0 && 
                    controller.checkEligibility(controller.getApplicant(), project, flatType)) {
                    
                    System.out.printf("%-20s %-15s %-15d $%-10.2f\n", 
                        project.getProjectName(),
                        flatType.getName(),
                        flatType.getUnitCount(),
                        flatType.getPrice());
                    
                    hasEligibleFlatTypes = true;
                }
            }
            
            if (!hasEligibleFlatTypes) {
                System.out.printf("%-20s %-15s %-15s %-10s\n", 
                    project.getProjectName(),
                    "No eligible flat types available",
                    "-",
                    "-");
            }
        }
        System.out.println("------------------------------------------------------");
    }
    

    public void displayAppliedProject(Project project, FlatType flatType, ApplicationStatus status) {
        System.out.println("Applied Project: " + project.getProjectName());
        System.out.println("Flat Type: " + flatType.getName());
        System.out.println("Status: " + status);
    }

    public void viewApplicationStatus(ApplicationStatus status) {
        System.out.println("Application Status: " + status);
    }


    
    public void viewAppliedProject(Applicant applicant) {
        System.out.println("\n===== Applied Project Details =====");
    
        Application app = applicant.getApplication();
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
    
    

    public void viewApplicationStatus(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            viewApplicationStatus(app.getStatus());
        } else {
            System.out.println("No application made yet.");
        }
    }


    private void submitApplicationMenu() {
        System.out.println("\n===== Submit Application =====");
    
        // Get eligible projects first
        List<Project> eligibleProjects = controller.getEligibleProjects(controller.getApplicant());
    
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects available for application.");
            return;
        }
    
        // Header for the project and flat type table
        System.out.println("------------------------------------------------------");
        System.out.printf("%-5s %-20s %-15s %-12s %-10s\n", "No.", "Project Name", "Flat Type", "Units Left", "Price (SGD)");
        System.out.println("------------------------------------------------------");
    
        // Display eligible projects with flat types and numbered list
        int projectCounter = 1;
        for (Project project : eligibleProjects) {
            boolean hasEligibleFlatTypes = false;
    
            // Loop through all flat types for the current project
            for (FlatType flatType : project.getFlatTypes()) {
                if (flatType.getUnitCount() > 0 && controller.checkEligibility(controller.getApplicant(), project, flatType)) {
                    // Display each flat type under its corresponding project with a number
                    System.out.printf("%-5d %-20s %-15s %-12d $%-10.2f\n", 
                            projectCounter,
                            project.getProjectName(),
                            flatType.getName(),
                            flatType.getUnitCount(),
                            flatType.getPrice());
                    hasEligibleFlatTypes = true;
                    projectCounter++;
                }
            }
    
            // If no eligible flat types are available for this project
            if (!hasEligibleFlatTypes) {
                System.out.printf("%-5d %-20s %-15s %-12s %-10s\n", 
                        projectCounter,
                        project.getProjectName(),
                        "No eligible flat types available",
                        "-",
                        "-");
                projectCounter++;
            }
        }
    
        System.out.println("------------------------------------------------------");
    
        // Prompt user to select a project by number
        System.out.print("Select project number: ");
        try {
            int projectNum = Integer.parseInt(scanner.nextLine().trim());
    
            // Validate project selection
            if (projectNum < 1 || projectNum > eligibleProjects.size()) {
                System.out.println("Invalid project selection.");
                return;
            }
    
            Project selectedProject = eligibleProjects.get(projectNum - 1);
    
            // Display available flat types for the selected project
            List<FlatType> availableFlatTypes = new ArrayList<>();
            for (FlatType ft : selectedProject.getFlatTypes()) {
                if (ft.getUnitCount() > 0 && controller.checkEligibility(controller.getApplicant(), selectedProject, ft)) {
                    availableFlatTypes.add(ft);
                }
            }
    
            if (availableFlatTypes.isEmpty()) {
                System.out.println("No available flat types for this project.");
                return;
            }
    
            // Show available flat types with prices
            System.out.println("\nAvailable Flat Types:");
            int index = 1;
            for (FlatType ft : availableFlatTypes) {
                System.out.println(index + ". " + ft.getName() + " ($" + ft.getPrice() + ")");
                index++;
            }
    
            // Get flat type selection
            System.out.print("Select flat type by number (1-" + availableFlatTypes.size() + "): ");
            try {
                int selectedNumber = Integer.parseInt(scanner.nextLine());
                if (selectedNumber < 1 || selectedNumber > availableFlatTypes.size()) {
                    System.out.println("Invalid selection.");
                    return;
                }
                FlatType selectedFlatType = availableFlatTypes.get(selectedNumber - 1);
                controller.submitApplication(controller.getApplicant(), selectedProject, selectedFlatType);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void submitEnquiry() {
        System.out.println("\n===== Submit Enquiry =====");

        // Display all visible projects
        List<Project> visibleProjects = controller.getVisibleProjects();

        if (visibleProjects.isEmpty()) {
            System.out.println("No projects available for enquiry.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display project list
        System.out.println("Available Projects:");
        System.out.println("ID | Project Name | Neighborhood");
        System.out.println("--------------------------------");

        int index = 1;
        for (Project project : visibleProjects) {
            System.out.printf("%2d | %-20s | %-15s\n",
                    index++,
                    project.getProjectName(),
                    project.getNeighborhood());
        }

        // Get project selection
        System.out.print("\nEnter project ID to enquire about (0 to cancel): ");
        try {
            int projectId = Integer.parseInt(scanner.nextLine().trim());

            if (projectId == 0) {
                return; // User canceled
            }

            if (projectId < 1 || projectId > visibleProjects.size()) {
                System.out.println("Invalid project ID. Please try again.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Get the selected project
            Project selectedProject = visibleProjects.get(projectId - 1);

            // Get enquiry content
            System.out.println("\nEnter your enquiry about " + selectedProject.getProjectName() + ":");
            String content = scanner.nextLine().trim();

            if (content.isEmpty()) {
                System.out.println("Enquiry cannot be empty. Operation cancelled.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Submit the enquiry
            controller.submitEnquiry(selectedProject, content);
            System.out.println("Enquiry submitted successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void viewEnquiries() {
        System.out.println("\n===== My Enquiries =====");

        List<Enquiry> enquiries = controller.getApplicantEnquiries();

        if (enquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries yet.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("ID | Project | Content | Response");
        System.out.println("----------------------------------");

        for (Enquiry enquiry : enquiries) {
            String responseStatus = enquiry.getResponse().isEmpty() ? "No response yet" : "Responded";
            System.out.printf("%2d | %-15s | %-30s | %s\n",
                    enquiry.getEnquiryID(),
                    enquiry.getProject().getProjectName(),
                    (enquiry.getContent().length() > 30 ? enquiry.getContent().substring(0, 27) + "..."
                            : enquiry.getContent()),
                    responseStatus);
        }

        // View detailed enquiry
        System.out.print("\nEnter enquiry ID to view details (0 to cancel): ");
        try {
            int enquiryId = Integer.parseInt(scanner.nextLine().trim());

            if (enquiryId == 0) {
                return; // User canceled
            }

            // Find the enquiry with the given ID
            Enquiry selectedEnquiry = null;
            for (Enquiry enquiry : enquiries) {
                if (enquiry.getEnquiryID() == enquiryId) {
                    selectedEnquiry = enquiry;
                    break;
                }
            }

            if (selectedEnquiry == null) {
                System.out.println("Enquiry not found. Please try again.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Display full enquiry details
            System.out.println("\n===== Enquiry Details =====");
            System.out.println("Enquiry ID: " + selectedEnquiry.getEnquiryID());
            System.out.println("Project: " + selectedEnquiry.getProject().getProjectName());
            System.out.println("Content: " + selectedEnquiry.getContent());
            System.out.println("Response: " +
                    (selectedEnquiry.getResponse().isEmpty() ? "No response yet" : selectedEnquiry.getResponse()));

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void editEnquiry() {
        System.out.println("\n===== Edit Enquiry =====");

        List<Enquiry> enquiries = controller.getApplicantEnquiries();

        if (enquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries yet.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("Your Enquiries:");
        System.out.println("ID | Project | Content | Response");
        System.out.println("----------------------------------");

        for (Enquiry enquiry : enquiries) {
            String responseStatus = enquiry.getResponse().isEmpty() ? "No response yet" : "Responded";
            System.out.printf("%2d | %-15s | %-30s | %s\n",
                    enquiry.getEnquiryID(),
                    enquiry.getProject().getProjectName(),
                    (enquiry.getContent().length() > 30 ? enquiry.getContent().substring(0, 27) + "..."
                            : enquiry.getContent()),
                    responseStatus);
        }

        // Select enquiry to edit
        System.out.print("\nEnter enquiry ID to edit (0 to cancel): ");
        try {
            int enquiryId = Integer.parseInt(scanner.nextLine().trim());

            if (enquiryId == 0) {
                return; // User canceled
            }

            // Find the enquiry with the given ID
            Enquiry selectedEnquiry = null;
            for (Enquiry enquiry : enquiries) {
                if (enquiry.getEnquiryID() == enquiryId) {
                    selectedEnquiry = enquiry;
                    break;
                }
            }

            if (selectedEnquiry == null) {
                System.out.println("Enquiry not found. Please try again.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Check if the enquiry has already been responded to
            if (!selectedEnquiry.getResponse().isEmpty()) {
                System.out.println("Cannot edit an enquiry that has already been responded to.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Display current content and get new content
            System.out.println("\nCurrent content: " + selectedEnquiry.getContent());
            System.out.print("Enter new content: ");
            String newContent = scanner.nextLine().trim();

            if (newContent.isEmpty()) {
                System.out.println("Enquiry content cannot be empty. Operation cancelled.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Update the enquiry
            controller.editEnquiry(selectedEnquiry, newContent);
            System.out.println("Enquiry updated successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void deleteEnquiry() {
        System.out.println("\n===== Delete Enquiry =====");

        List<Enquiry> enquiries = controller.getApplicantEnquiries();

        if (enquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries yet.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("Your Enquiries:");
        System.out.println("ID | Project | Content | Response");
        System.out.println("----------------------------------");

        for (Enquiry enquiry : enquiries) {
            String responseStatus = enquiry.getResponse().isEmpty() ? "No response yet" : "Responded";
            System.out.printf("%2d | %-15s | %-30s | %s\n",
                    enquiry.getEnquiryID(),
                    enquiry.getProject().getProjectName(),
                    (enquiry.getContent().length() > 30 ? enquiry.getContent().substring(0, 27) + "..."
                            : enquiry.getContent()),
                    responseStatus);
        }

        // Select enquiry to delete
        System.out.print("\nEnter enquiry ID to delete (0 to cancel): ");
        try {
            int enquiryId = Integer.parseInt(scanner.nextLine().trim());

            if (enquiryId == 0) {
                return; // User canceled
            }

            // Find the enquiry with the given ID
            Enquiry selectedEnquiry = null;
            for (Enquiry enquiry : enquiries) {
                if (enquiry.getEnquiryID() == enquiryId) {
                    selectedEnquiry = enquiry;
                    break;
                }
            }

            if (selectedEnquiry == null) {
                System.out.println("Enquiry not found. Please try again.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Check if the enquiry has already been responded to
            if (!selectedEnquiry.getResponse().isEmpty()) {
                System.out.println("Cannot delete an enquiry that has already been responded to.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Confirm deletion
            System.out.print("\nAre you sure you want to delete this enquiry? (y/n): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("y") || confirmation.equals("yes")) {
                // Delete the enquiry
                controller.deleteEnquiry(selectedEnquiry);
                System.out.println("Enquiry deleted successfully!");
            } else {
                System.out.println("Deletion cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

}