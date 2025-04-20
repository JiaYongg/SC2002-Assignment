import java.util.List;
import java.util.Scanner;

public class EnquiryView {
    private EnquiryController controller;
    private Scanner scanner;
    private Applicant applicant;

    public EnquiryView(EnquiryController controller, Applicant applicant) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
        this.applicant = applicant;
    }

    // Applicant submits an enquiry on a project
    public void showEnquiryForm() {
        List<Project> projects = controller.getAllProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        System.out.println("Available Projects:");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getProjectName());
        }

        System.out.print("Choose project by number: ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice < 1 || choice > projects.size()) {
            System.out.println("Invalid project choice.");
            return;
        }

        Project project = projects.get(choice - 1);

        System.out.print("Enter your enquiry content: ");
        String content = scanner.nextLine();

        controller.submitEnquiry(applicant, project, content);
    }

    // View response to an enquiry
    public void displayEnquiryResponse(String response) {
        System.out.println("===== Enquiry Response =====");
        System.out.println((response == null || response.isEmpty()) ? "No reply yet." : response);
    }

    // Applicant can only manage their own enquiries
    public void manageEnquiry(Scanner scanner) {
        if (applicant.getEnquiries().isEmpty()) {
            System.out.println("You have no enquiries.");
            return;
        }

        List<Project> projects = controller.getAllProjects();

        System.out.println("Available Projects:");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getProjectName());
        }

        System.out.print("Choose project by number: ");
        int projectChoice = Integer.parseInt(scanner.nextLine());
        if (projectChoice < 1 || projectChoice > projects.size()) {
            System.out.println("Invalid project choice.");
            return;
        }

        Project project = projects.get(projectChoice - 1);

        System.out.println("Your Enquiries:");
        for (Enquiry enq : applicant.getEnquiries()) {
            System.out.println("ID: " + enq.getEnquiryID() + " | Project: " + enq.getProject().getProjectName() +
                    " | Content: " + enq.getContent() + " | Reply: " + (enq.getResponse().isEmpty() ? "No reply" : enq.getResponse()));
        }

        System.out.print("Enter Enquiry ID to manage: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.println("1. Edit Enquiry\n2. Delete Enquiry");
        int option = Integer.parseInt(scanner.nextLine());

        switch (option) {
            case 1 -> {
                System.out.print("Enter new content: ");
                String newContent = scanner.nextLine();
                controller.editEnquiry(applicant, id, newContent);
            }
            case 2 -> controller.deleteEnquiry(applicant, project, id);
            default -> System.out.println("Invalid choice.");
        }
    }

    // Still allows manager to view all enquiries (unchanged)
    public void viewEnquiries() {
        List<Project> projects = controller.getAllProjects();
        List<HDBManager> managers = new java.util.ArrayList<>();

        for (Project p : projects) {
            HDBManager manager = p.getManagerInCharge();
            if (manager != null && !managers.contains(manager)) {
                managers.add(manager);
            }
        }

        System.out.println("Available Managers:");
        for (int i = 0; i < managers.size(); i++) {
            System.out.println((i + 1) + ". " + managers.get(i).getName());
        }

        System.out.print("Choose manager by number: ");
        int managerChoice = Integer.parseInt(scanner.nextLine());

        if (managerChoice >= 1 && managerChoice <= managers.size()) {
            controller.viewEnquiryByManager(managers.get(managerChoice - 1));
        } else {
            System.out.println("Invalid manager selected.");
        }
    }
}
