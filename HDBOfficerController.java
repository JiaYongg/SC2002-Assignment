import java.util.ArrayList;
import java.util.List;

public class HDBOfficerController {
    private HDBOfficer currentOfficer;
    private List<Project> allProjects;
    
    public HDBOfficerController(HDBOfficer officer) {
        this.currentOfficer = officer;
        this.allProjects = new ArrayList<>();
        // In a full implementation, you would load projects from a file
    }
    
    // public List<Project> getVisibleProjects() {
    //     List<Project> visibleProjects = new ArrayList<>();
    //     for (Project project : allProjects) {
    //         if (project.isVisible() && currentOfficer.isEligibleForProject(project)) {
    //             visibleProjects.add(project);
    //         }
    //     }
    //     return visibleProjects;
    // }
    
    // public List<Project> getAvailableProjectsForRegistration() {
    //     List<Project> availableProjects = new ArrayList<>();
    //     for (Project project : allProjects) {
    //         if (project.getRemainingOfficerSlots() > 0 && 
    //             currentOfficer.canRegisterForProject(project)) {
    //             availableProjects.add(project);
    //         }
    //     }
    //     return availableProjects;
    // }
    
    // public boolean registerForProject(Project project) {
    //     if (!currentOfficer.canRegisterForProject(project)) {
    //         return false;
    //     }
        
    //     OfficerRegistration registration = new OfficerRegistration(currentOfficer, project);
    //     currentOfficer.addRegistration(registration);
    //     project.addOfficerRegistration(registration);
    //     return true;
    // }
    
    public List<OfficerRegistration> getRegistrations() {
        return currentOfficer.getRegistrations();
    }
    
    public Project getAssignedProject() {
        return currentOfficer.getAssignedProject();
    }
    
    // public List<Enquiry> getProjectEnquiries() {
    //     if (currentOfficer.getAssignedProject() == null) {
    //         return new ArrayList<>();
    //     }
        
    //     return currentOfficer.getAssignedProject().getEnquiries();
    // }
    
    // public boolean replyToEnquiry(Enquiry enquiry, String reply) {
    //     if (currentOfficer.getAssignedProject() == null || 
    //         !enquiry.getProject().equals(currentOfficer.getAssignedProject())) {
    //         return false;
    //     }
        
    //     enquiry.setReply(reply);
    //     return true;
    // }
    
    // public Application getApplicationByNric(String nric) {
    //     if (currentOfficer.getAssignedProject() == null) {
    //         return null;
    //     }
        
    //     // This is a simplified implementation
    //     // In a real system, you would search through all applications for the project
    //     return null;
    // }
    
    // public boolean updateApplicationStatus(Application application, String newStatus) {
    //     if (currentOfficer.getAssignedProject() == null || 
    //         !application.getProject().equals(currentOfficer.getAssignedProject())) {
    //         return false;
    //     }
        
    //     application.setStatus(newStatus);
    //     return true;
    // }
    
    public HDBOfficer getCurrentOfficer() {
        return currentOfficer;
    }
}
