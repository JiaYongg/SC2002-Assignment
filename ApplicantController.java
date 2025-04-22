import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ApplicantController {
    private Applicant applicant;
    private List<Project> allProjects;
    private ProjectFileReader projectReader;
    private EnquiryController enquiryController;
    private ApplicationFileWriter applicationWriter;

    public ApplicantController(Applicant applicant) {
        this.applicant = applicant;
        this.projectReader = new ProjectFileReader();
        this.allProjects = new ArrayList<>(projectReader.readFromFile().values());
        this.enquiryController = new EnquiryController(allProjects);
        this.enquiryController.linkEnquiriesTo(applicant);
        this.applicationWriter = new ApplicationFileWriter();
    }

    public Applicant getApplicant() {
        return applicant;
    }

    // In your ApplicantController
    public void submitApplication(Applicant applicant, Project project, FlatType flatType) {
        ApplicationController applicationController = new ApplicationController();
        boolean success = applicationController.applyForProject(applicant, project, flatType);

        if (success != true) {
            System.out.println("Failed to submit application.");
        } 
    }

    public List<Project> getEligibleProjects(Applicant applicant) {
        List<Project> eligibleProjects = new ArrayList<>();

        for (Project p : allProjects) {
            if (!p.isVisible())
                continue;

            for (FlatType ft : p.getFlatTypes()) {
                if (ft.getUnitCount() > 0 && checkEligibility(applicant, p, ft)) {
                    eligibleProjects.add(p);
                    break;
                }
            }
        }

        return eligibleProjects;
    }

    public List<ProjectFlatTypeInfo> getEligibleProjectsWithDetails(Applicant applicant) {
        List<ProjectFlatTypeInfo> eligibleDetails = new ArrayList<>();

        for (Project p : allProjects) {
            if (!p.isVisible())
                continue;

            for (FlatType ft : p.getFlatTypes()) {
                if (ft.getUnitCount() > 0 && checkEligibility(applicant, p, ft)) {
                    eligibleDetails.add(new ProjectFlatTypeInfo(p, ft));
                }
            }
        }

        return eligibleDetails;
    }

    // Helper class to hold project and flat type information together
    public class ProjectFlatTypeInfo {
        private Project project;
        private FlatType flatType;

        public ProjectFlatTypeInfo(Project project, FlatType flatType) {
            this.project = project;
            this.flatType = flatType;
        }

        public Project getProject() {
            return project;
        }

        public FlatType getFlatType() {
            return flatType;
        }
    }

    public FlatType selectFlatType(Project project, Applicant applicant) {
        List<FlatType> eligibleFlatTypes = new ArrayList<>();

        // Get all eligible flat types for this project and applicant
        for (FlatType ft : project.getFlatTypes()) {
            if (ft.getUnitCount() > 0 && checkEligibility(applicant, project, ft)) {
                eligibleFlatTypes.add(ft);
            }
        }

        if (eligibleFlatTypes.isEmpty()) {
            System.out.println("No eligible flat types available for this project.");
            return null;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAvailable Flat Types:");

        for (int i = 0; i < eligibleFlatTypes.size(); i++) {
            FlatType ft = eligibleFlatTypes.get(i);
            System.out.printf("%d. %s ($%.2f)\n", (i + 1), ft.getName(), ft.getPrice());
        }

        System.out.print("\nEnter your choice (1-" + eligibleFlatTypes.size() + "): ");

        try {
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= eligibleFlatTypes.size()) {
                return eligibleFlatTypes.get(choice - 1);
            } else {
                System.out.println("Invalid choice. Please select a number between 1 and " + eligibleFlatTypes.size());
                return selectFlatType(project, applicant); // Recursive call for invalid input
            }
        } catch (Exception e) {
            scanner.nextLine(); // Clear the scanner buffer
            System.out.println("Invalid input. Please enter a number.");
            return selectFlatType(project, applicant); // Recursive call for invalid input
        }
    }

    public boolean checkEligibility(Applicant applicant, Project project, FlatType flatType) {
        // Check if project is visible and within application period
        Date currentDate = new Date();
        if (!project.isVisible() ||
                currentDate.before(project.getApplicationOpenDate()) ||
                currentDate.after(project.getApplicationCloseDate())) {
            return false;
        }

        // Check if flat type has available units
        if (flatType.getUnitCount() <= 0) {
            return false;
        }

        // Check applicant eligibility
        int age = applicant.getAge();
        String maritalStatus = applicant.getMaritalStatus();

        if (maritalStatus.equalsIgnoreCase("Single")) {
            return age >= 35 && flatType.getName().equalsIgnoreCase("2-Room");
        } else if (maritalStatus.equalsIgnoreCase("Married")) {
            return age >= 21 && (flatType.getName().equalsIgnoreCase("2-Room") ||
                    flatType.getName().equalsIgnoreCase("3-Room"));
        }

        return false;
    }

    public void withdraw(Application app) {
        if (app != null) {
            app.setStatus(ApplicationStatus.PENDING);
            WithdrawalRequest req = new WithdrawalRequest(app);
            System.out.println("Withdrawal request submitted. Pending approval.");
        } else {
            System.out.println("No application found.");
        }
    }

    public List<Project> getVisibleProjects() {
        List<Project> visibleProjects = new ArrayList<>();
        for (Project p : allProjects) {
            if (p.isVisible()) {
                visibleProjects.add(p);
            }
        }
        return visibleProjects;
    }

    public void submitEnquiry(Project project, String content) {
        enquiryController.submitEnquiry(applicant, project, content);
    }

    public List<Enquiry> getApplicantEnquiries() {
        return applicant.getEnquiries();
    }

    public void editEnquiry(Enquiry enquiry, String newContent) {
        enquiry.updateContent(newContent);
    }

    public void deleteEnquiry(Enquiry enquiry) {
        applicant.getEnquiries().remove(enquiry);
        enquiry.getProject().getEnquiries().remove(enquiry);
    }
}
