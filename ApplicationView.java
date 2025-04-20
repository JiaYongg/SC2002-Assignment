public class ApplicationView {
    private ApplicationController controller;

    public ApplicationView(ApplicationController controller) {
        this.controller = controller;
    }

    public void promptProjectApplication() {
        System.out.println("=== Apply for a BTO Project ===");
        System.out.println("Please enter project name and flat type.");
    }

    public void showApplicationStatus(ApplicationStatus status) {
        System.out.println("Application Status: " + status);
    }

    public void displayAppliedProject(Project project, FlatType flatType, ApplicationStatus status) {
        System.out.println("=== Applied Project Details ===");
        System.out.println("Project: " + project.getProjectName());
        System.out.println("Flat Type: " + flatType.getName());
        System.out.println("Status: " + status);
    }

    public void displayApplicationStatus(ApplicationStatus status) {
        System.out.println("Application Status: " + status);
    }
}
