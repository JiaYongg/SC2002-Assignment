import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends Applicant {
    private Project assignedProject;
    private List<OfficerRegistration> registrations;
    private List<Project> handledProjects;
    
    public HDBOfficer(List<Project> handledProjects) {
        super();
        this.registrations = new ArrayList<>();
        this.handledProjects = handledProjects;
    }
    
    public List<Project> getHandledProjects() {
        return handledProjects;
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
    
    public boolean canRegisterForProject(Project project) {
          //Cannot register if already assigned to a project
         if (isAssignedToProject()) {
             return false;
         }
        
         // Cannot register if already applied for this project as an applicant
         Application app = getApplication();
         if (app != null && app.getProject().equals(project)) {
             return false;
         }
        
         // Cannot register if already registered for another project with overlapping dates
         for (OfficerRegistration reg : registrations) {
             if (reg.getRegistrationStatus().equals("Approved") && 
                 reg.getProject().isOverlappingWith(project)) {
                 return false;
             }
         }
        
         return true;
     }
}
