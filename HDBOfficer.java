import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends Applicant {
    private Project assignedProject;
    private List<OfficerRegistration> registrations;
    
    public HDBOfficer() {
        super();
        this.registrations = new ArrayList<>();
    }
    
    public Project getAssignedProject() {
        return assignedProject;
    }
    
    public void setAssignedProject(Project assignedProject) {
        this.assignedProject = assignedProject;
    }
    
    public List<OfficerRegistration> getRegistrations() {
        return registrations;
    }
    
    public void addRegistration(OfficerRegistration registration) {
        this.registrations.add(registration);
    }
    
    public boolean isAssignedToProject() {
        return assignedProject != null;
    }
    
    // public boolean canRegisterForProject(Project project) {
    //      Cannot register if already assigned to a project
    //     if (isAssignedToProject()) {
    //         return false;
    //     }
        
    //     // Cannot register if already applied for this project as an applicant
    //     for (Application app : getApplications()) {
    //         if (app.getProject().equals(project)) {
    //             return false;
    //         }
    //     }
        
    //     // Cannot register if already registered for another project with overlapping dates
    //     for (OfficerRegistration reg : registrations) {
    //         if (reg.getStatus().equals("Approved") && 
    //             reg.getProject().isOverlappingWith(project)) {
    //             return false;
    //         }
    //     }
        
    //     return true;
    // }
}
