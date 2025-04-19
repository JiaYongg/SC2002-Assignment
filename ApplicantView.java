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
                        controller.showEligibleProjects(controller.getApplicant());
                        break;
                    case 2: 
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
                            break;
                        }

                        System.out.print("Enter Flat Type: ");
                        String flatTypeName = scanner.nextLine().trim();
                        FlatType flatType = selected.getFlatTypeByName(flatTypeName);

                        if (flatType == null || !controller.checkEligibility(controller.getApplicant(), flatType)) {
                            System.out.println("Invalid flat type or not eligible.");
                            break;
                        }

                        controller.submitApplication(controller.getApplicant(), selected, flatType);
                        break;
                    case 3: 
                        controller.viewAppliedProject(controller.getApplicant());
                        break;
                    case 4: 
                        controller.viewApplicationStatus(controller.getApplicant());
                        break;
                    case 5: 
                        //withdraw;
                        break;
                    case 0:
                        logout = false;
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

    public void displayAppliedProject(Project project, FlatType flatType, String status) {
        System.out.println("Applied Project: " + project.getProjectName());
        System.out.println("Flat Type: " + flatType.getName());
        System.out.println("Status: " + status);
    }

    public void viewApplicationStatus(String status) {
        System.out.println("Application Status: " + status);
    }
}
