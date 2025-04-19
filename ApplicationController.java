public class ApplicationController {
    private ApplicationView view;

    public ApplicationController(ApplicationView view) {
        this.view = view;
    }

    public void applyForProject(Applicant applicant, Project project, FlatType flatType) {

    }

    public void withdrawApplication(Applicant applicant) {

    }

    public ApplicationStatus getApplicationStatus(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            
        }
    }
}
