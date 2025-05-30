import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HDBManager extends User {
    private List<Project> managedProjects;
    
    public HDBManager() {
        super();
        this.managedProjects = new ArrayList<>();
    }
    
    
    public List<Project> getManagedProjects() {
        return new ArrayList<>(managedProjects);
    }
    
    public void addManagedProject(Project project) {
        this.managedProjects.add(project);
    }
    
    public void removeManagedProject(Project project) {
        this.managedProjects.remove(project);
    }
    
    
    public boolean canHandleProjectInPeriod(Date openDate, Date closeDate) {
        for (Project project : managedProjects) {
            
            if (!(closeDate.before(project.getApplicationOpenDate()) || 
                  openDate.after(project.getApplicationCloseDate()))) {
                return false;
            }
        }
        return true;
    }
    
    public void viewEnquiries() {
        System.out.println("Manager " + getName() + " is viewing enquiries...");
        
    }
    
    public String getAccessLevel() {
        return "HIGH";
    }
    
    public void generateBookingReport() {
        System.out.println("Generating booking report for all projects managed by " + getName());
        
    }
}
