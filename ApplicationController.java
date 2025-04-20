public class ApplicationController {
    private ApplicationView view;

    public ApplicationController(ApplicationView view) {
        this.view = view;
    }

    public void applyForProject(Applicant applicant, Project project, FlatType flatType) {
        view.promptProjectApplication();
        Application application = new Application(applicant, project, flatType, ApplicationStatus.SUBMITTED);
        applicant.setApplication(application);
        view.displayAppliedProject(project, flatType, application.getStatus().name());
    }

    public void withdrawApplication(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null && app.getStatus() != ApplicationStatus.BOOKED) {
            app.addWithdrawalRequest(new java.util.Date(), WithdrawalStatus.PENDING);
            System.out.println("Withdrawal request submitted. Pending manager approval.");
        } else {
            System.out.println("No active application or already booked. Cannot withdraw.");
        }
    }

    public ApplicationStatus getApplicationStatus(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            view.displayApplicationStatus(app.getStatus().name());
            return app.getStatus();
        } else {
            System.out.println("No application found.");
            return null;
        }
    }
}
