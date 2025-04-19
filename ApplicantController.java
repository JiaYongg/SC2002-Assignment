import java.util.List;

public class ApplicantController {
    private Applicant currentApplicant;
    private ApplicantView applicantView;


    public ApplicantController(Applicant applicant, ApplicantView applicantView) {
        this.currentApplicant = applicant;
        this.applicantView = applicantView;
    }

    //additional methods
    public Applicant getApplicant() {
        return currentApplicant;
    }

    //following class diagram
    public void showEligibleProjects(Applicant applicant) {
        List<Project> eligibleProjects = getEligibleProjects(applicant);
        applicantView.displayEligibleProjects(eligibleProjects);
    }

    public void submitApplication(Applicant applicant, Project project, FlatType flatType) {
        Application app = applicant.getApplication();
        if (app != null) {
            applicantView.displayAppliedProject(app.getProject(), app.getFlatType(), app.getStatus());
        }
    }

    public void viewApplicationStatus(String status) {
        applicantView.viewApplicationStatus(status);
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
