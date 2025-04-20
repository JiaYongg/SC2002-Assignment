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
            System.out.println("3. View Applied Project");
            System.out.println("4. View Application Status");
            System.out.println("5. Withdraw Application");
            System.out.println("0. Logout");
    
            System.out.print("Enter your choice: ");

            try {  
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1: 
                        showEligibleProjects(controller.getApplicant());
                        break;
                    case 2: 
                        apply(controller.getApplicant());
                        break;
                    case 3: 
                        viewAppliedProject(controller.getApplicant());
                        break;
                    case 4: 
                        viewApplicationStatus(controller.getApplicant());
                        break;
                    case 5: 
                        controller.withdraw(controller.getApplicant().getApplication());
                        break;
                    case 0:
                        logout = true;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return logout;
    }

    public void displayEligibleProjects(List<Project> projects) {
        for (Project project : projects) {
            System.out.println("Project: " + project.getProjectName());
        }
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
        Application app = applicant.getApplication();
        if (app != null) {
            displayAppliedProject(app.getProject(), app.getFlatType(), app.getStatus());
        } else {
            System.out.println("No project applied yet.");
        }
    }

    public void viewApplicationStatus(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            viewApplicationStatus(app.getStatus()); 
        }
        else {
            System.out.println("No application made yet.");
        }
    }

    public void showEligibleProjects(Applicant applicant) {
        List<Project> eligibleProjects = controller.getEligibleProjects(applicant);
        displayEligibleProjects(eligibleProjects);
    }

    public void apply(Applicant applicant) {
        System.out.println("===== Submit Application =====");
        System.out.print("Enter Project Name: ");
        String projName = scanner.nextLine().trim();

        List<Project> projects = controller.getEligibleProjects(controller.getApplicant());
        Project selected = null;
        for (Project p : projects) {
            if (p.getProjectName().equalsIgnoreCase(projName)) {
                selected = p;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Project not found or not eligible.");
            return;
        }

        System.out.print("Enter Flat Type: ");
        String flatTypeName = scanner.nextLine().trim();
        FlatType flatType = selected.getFlatTypeByName(flatTypeName);

        if (flatType == null || !controller.checkEligibility(controller.getApplicant(), flatType)) {
            System.out.println("Invalid flat type or not eligible.");
            return;
        }

        controller.submitApplication(controller.getApplicant(), selected, flatType);    }
}