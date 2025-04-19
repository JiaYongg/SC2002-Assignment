import java.util.List;

public class ApplicantController {
    private Applicant currentApplicant;
    private ApplicantView applicantView;


    public ApplicantController(Applicant applicant, ApplicantView applicantView) {
        this.currentApplicant = applicant;
        this.applicantView = applicantView;
    }

    public Applicant getApplicant() {
        return currentApplicant;
    }

    public void showEligibleProjects(Applicant applicant) {
        List<Project> eligibleProjects = getEligibleProjects(applicant);
        applicantView.displayEligibleProjects(eligibleProjects);
    }

    public void submitApplication(Applicant applicant, Project project, FlatType flatType) {
        Application app = new Application(applicant,project,flatType, "SUBMITTED"); //incomplete
        applicant.setApplication(app);
    }

    public void viewApplicationStatus(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            applicantView.viewApplicationStatus(app.getStatus());
        }
        else {
            applicantView.viewApplicationStatus("No application found.");
        }
    }

    public void viewAppliedProject(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            applicantView.displayAppliedProject(app.getProject(), app.getFlatType(), app.getStatus());
        }
    }
    
    public List<Project> getEligibleProjects(Applicant applicant) {
        return ProjectController.getAllProjects(); // Assumes static method exists
    }

    public boolean checkEligibility(Applicant applicant, FlatType flatType) {
        return flatType.isAvail();
    }

    public void apply(Applicant applicant, Project project, FlatType flatType) {
        submitApplication(applicant, project, flatType);
    }

    public void withdraw(Application app) {
        app.setStatus("Withdrawn");
    }
}
