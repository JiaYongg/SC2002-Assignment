import java.util.ArrayList;
import java.util.List;

public class ApplicantController {
    private Applicant applicant;
    private List<Project> allProjects;
    private ProjectFileReader projectReader;

    public ApplicantController(Applicant applicant) {
        this.applicant = applicant;
        this.projectReader = new ProjectFileReader();
        this.allProjects = new ArrayList<>(projectReader.readFromFile().values());
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void submitApplication(Applicant applicant, Project project, FlatType flatType) {
        Application app = new Application(applicant,project,flatType); 
        applicant.setApplication(app);
    }
    
    public List<Project> getEligibleProjects(Applicant applicant) {
        List<Project> eligibleProjects = new ArrayList<>();

        for (Project p : allProjects) {
            if (!p.isVisible()) continue;

            for (FlatType ft : p.getFlatTypes()) {
                if (ft.getUnitCount() > 0 && checkEligibility(applicant, ft)) {
                    eligibleProjects.add(p);
                    break;
                }
            }
        }
        return eligibleProjects;
    }

    public boolean checkEligibility(Applicant applicant, FlatType flatType) {
        int age = applicant.getAge();
        String maritalStatus = applicant.getMaritalStatus();

        if (maritalStatus.equalsIgnoreCase("Single")) {
            return age >= 35 && flatType.getName().equalsIgnoreCase("2-Room");
        }
        else if (maritalStatus.equalsIgnoreCase("Married")) {
            return age >= 21 && (flatType.getName().equalsIgnoreCase("2-Room") || flatType.getName().equalsIgnoreCase("3-Room"));
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
        }    }

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
            Enquiry enquiry = new Enquiry(applicant, project, content);
            applicant.addEnquiry(enquiry);
            project.addEnquiry(enquiry);
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
