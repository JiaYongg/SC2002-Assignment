public class ApplicationController {
    private Application application;

    public ApplicationController(Application application) {
        this.application = application;
    }

    public void applyForProject(Applicant applicant, Project project, FlatType flatType) {
        Application app = new Application(applicant, project, flatType);
        applicant.setApplication(app);
    }

    public void withdrawApplication(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            WithdrawalRequest req = new WithdrawalRequest(app);
            System.out.println("Withdrawal request submitted. Pending approval.");
        } else {
            System.out.println("No application found.");
        }
    }

    public ApplicationStatus getApplicationStatus(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            return app.getStatus();
        } else {
            System.out.println("No application found.");
            return null;
        }
    }
}
