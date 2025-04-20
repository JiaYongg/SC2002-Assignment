import java.util.ArrayList;
import java.util.List;

public class EnquiryController {
    private List<Applicant> allApplicants;
    private List<Project> allProjects;
    private ProjectFileReader projectReader;
    private ApplicantFileReader applicantReader;

    public EnquiryController() {
        // Initialize readers
        this.projectReader = new ProjectFileReader();
        this.applicantReader = new ApplicantFileReader();

        // Load projects
        this.allProjects = new ArrayList<>(projectReader.readFromFile().values());

        // Load applicants (cast from Map<String, User> to List<Applicant>)
        this.allApplicants = new ArrayList<>();
        for (User user : applicantReader.readFromFile().values()) {
            if (user instanceof Applicant) {
                allApplicants.add((Applicant) user);
            }
        }
    }

    public List<Project> getAllProjects() {
        return allProjects;
    }

    public List<Applicant> getAllApplicants() {
        return allApplicants;
    }

    public void submitEnquiry(Applicant applicant, Project project, String content) {
        Enquiry enq = new Enquiry(applicant, project, content);
        applicant.getEnquiries().add(enq);
        project.addEnquiry(enq);
        System.out.println("Enquiry submitted with ID: " + enq.getEnquiryID());
    }

    public void editEnquiry(Applicant applicant, int enquiryId, String newContent) {
        for (Enquiry enq : applicant.getEnquiries()) {
            if (enq.getEnquiryID() == enquiryId) {
                enq.updateContent(newContent);
                System.out.println("Enquiry ID " + enquiryId + " updated.");
                return;
            }
        }
        System.out.println("Enquiry not found.");
    }

    public void deleteEnquiry(Applicant applicant, Project project, int enquiryId) {
        Enquiry target = null;

        for (Enquiry enq : applicant.getEnquiries()) {
            if (enq.getEnquiryID() == enquiryId) {
                target = enq;
                break;
            }
        }

        if (target != null) {
            applicant.getEnquiries().remove(target);
            project.getEnquiries().remove(target);
            System.out.println("Enquiry ID " + enquiryId + " deleted.");
        } else {
            System.out.println("Enquiry not found.");
        }
    }

    public void replyToEnquiry(Project project, int enquiryId, String reply) {
        for (Enquiry enq : project.getEnquiries()) {
            if (enq.getEnquiryID() == enquiryId) {
                enq.setResponse(reply);
                System.out.println("Replied to Enquiry ID " + enquiryId);
                return;
            }
        }
        System.out.println("Enquiry not found.");
    }

    public void viewEnquiryByManager(HDBManager manager) {
        System.out.println("===== Enquiries for Manager: " + manager.getName() + " =====");
        for (Project project : manager.getManagedProjects()) {
            if (project.getEnquiries() == null || project.getEnquiries().isEmpty()) continue;

            for (Enquiry enq : project.getEnquiries()) {
                System.out.println("Enquiry ID: " + enq.getEnquiryID());
                System.out.println("Applicant: " + enq.getApplicant().getName());
                System.out.println("Project: " + enq.getProject().getProjectName());
                System.out.println("Content: " + enq.getContent());
                System.out.println("Reply: " + (enq.getResponse().isEmpty() ? "No reply yet" : enq.getResponse()));
                System.out.println("------------------------");
            }
        }
    }
}
