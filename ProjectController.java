/**
 * Controller class for Project operations in the BTO Management System.
 * Handles project-specific business logic including visibility rules.
 */
import java.util.Date;


public class ProjectController {

    public boolean updateProjectVisibility(Project project) {
        
        if (!project.isVisible()) {
            return false;
        }
        
        Date currentDate = new Date();
        boolean shouldBeVisible = true;
        
        
        if (currentDate.after(project.getApplicationCloseDate())) {
            shouldBeVisible = false;
        }
        
        
        boolean hasAvailableFlats = false;
        for (FlatType flatType : project.getFlatTypes()) {
            if (flatType.getUnitCount() > 0) {
                hasAvailableFlats = true;
                break;
            }
        }
        
        if (!hasAvailableFlats) {
            shouldBeVisible = false;
        }
        
        
        if (!shouldBeVisible) {
            project.setVisibility(false);
            return true;
        }
        
        return false;
    }
    

    /**
     * Checks if the project can be made visible based on business rules
     * 
     * @param project The project to check
     * @return true if the project can be made visible, false otherwise with reason
     */
    public VisibilityCheckResult canMakeVisible(Project project) {
        Date currentDate = new Date();

        
        if (currentDate.after(project.getApplicationCloseDate())) {
            return new VisibilityCheckResult(false,
                    "Cannot make project visible because the application period has ended. " +
                            "You must extend the application closing date before making the project visible.");
        }

        
        if (!hasAvailableFlats(project)) {
            return new VisibilityCheckResult(false,
                    "Cannot make project visible because there are no available flats. " +
                            "You must add more flat units before making the project visible.");
        }

        return new VisibilityCheckResult(true, "");
    }

    /**
     * Checks if a project has any available flats
     * 
     * @param project The project to check
     * @return true if any flat type has available units, false otherwise
     */
    public boolean hasAvailableFlats(Project project) {
        for (FlatType flatType : project.getFlatTypes()) {
            if (flatType.getUnitCount() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a project is currently open for applications
     * 
     * @param project The project to check
     * @return ProjectStatus object containing status and reason
     */
    public ProjectStatus getProjectStatus(Project project) {
        Date currentDate = new Date();
        boolean datesAreValid = currentDate.after(project.getApplicationOpenDate()) &&
                currentDate.before(project.getApplicationCloseDate());
        boolean hasFlats = hasAvailableFlats(project);
        boolean isVisible = project.isVisible();

        
        
        boolean isOpen = datesAreValid && hasFlats && isVisible;

        if (!isOpen) {
            if (!isVisible) {
                return new ProjectStatus(false, "CLOSED - Project is hidden from applicants");
            } else if (!datesAreValid) {
                if (currentDate.before(project.getApplicationOpenDate())) {
                    return new ProjectStatus(false, "CLOSED - Application period has not started yet");
                } else {
                    return new ProjectStatus(false, "CLOSED - Application period has ended");
                }
            } else if (!hasFlats) {
                return new ProjectStatus(false, "CLOSED - No available flats remaining");
            } else {
                return new ProjectStatus(false, "CLOSED for applications");
            }
        } else {
            return new ProjectStatus(true, "OPEN for applications");
        }
    }

    /**
     * Helper class to return visibility check results
     */
    public class VisibilityCheckResult {
        private boolean canMakeVisible;
        private String reason;

        public VisibilityCheckResult(boolean canMakeVisible, String reason) {
            this.canMakeVisible = canMakeVisible;
            this.reason = reason;
        }

        public boolean canMakeVisible() {
            return canMakeVisible;
        }

        public String getReason() {
            return reason;
        }
    }

    /**
     * Helper class to return project status information
     */
    public class ProjectStatus {
        private boolean isOpen;
        private String statusMessage;

        public ProjectStatus(boolean isOpen, String statusMessage) {
            this.isOpen = isOpen;
            this.statusMessage = statusMessage;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public String getStatusMessage() {
            return statusMessage;
        }
    }

    /**
     * Checks if a project is considered "active" based on business rules
     * A project is active if its close date has not passed OR it is visible to
     * applicants
     * 
     * @param project The project to check
     * @return true if the project is active, false otherwise
     */
    public boolean isProjectActive(Project project) {
        if (project == null) {
            return false;
        }

        Date currentDate = new Date();
        boolean closeDatePassed = currentDate.after(project.getApplicationCloseDate());
        boolean isHidden = !project.isVisible();

        
        
        
        return !closeDatePassed || !isHidden;
    }
}
