import java.util.ArrayList;
import java.util.List;

public class ApplicantController {
    private Applicant applicant;
    private ApplicantView applicantView;


    public ApplicantController(Applicant applicant, ApplicantView applicantView) {
        this.applicant = applicant;
        this.applicantView = applicantView;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void showEligibleProjects(Applicant applicant) {
        List<Project> eligibleProjects = getEligibleProjects(applicant);
        applicantView.displayEligibleProjects(eligibleProjects);
    }

    public void submitApplication(Applicant applicant, Project project, FlatType flatType) {
        Application app = new Application(applicant,project,flatType, "Pending"); //incomplete
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
        //INCOMPLETE
    }

    public boolean checkEligibility(Applicant applicant, FlatType flatType) {
        int age = applicant.getAge();
        String martialStatus = applicant.getMaritalStatus();

        if (martialStatus.equalsIgnoreCase("Single")) {
            return age >= 35 && flatType.getName().equalsIgnoreCase("2-Room");
        }
        else if (martialStatus.equalsIgnoreCase("Married")) {
            return age >= 21 && (flatType.getName().equalsIgnoreCase("2-Room") || flatType.getName().equalsIgnoreCase("3-Room"));
        }
        return false;
    }

    public void apply(Applicant applicant, Project project, FlatType flatType) {
        submitApplication(applicant, project, flatType);
    }

    public void withdraw(Application app) {
        app.setStatus("Withdrawn");
    }
}
