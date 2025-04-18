import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Manager extends User {
    private List<Project> managedProjects;
    
    public Manager() {
        super();
        this.managedProjects = new ArrayList<>();
    }
    
    // Project management methods
    public List<Project> getManagedProjects() {
        return new ArrayList<>(managedProjects);
    }
    
    public void addManagedProject(Project project) {
        this.managedProjects.add(project);
    }
    
    public void removeManagedProject(Project project) {
        this.managedProjects.remove(project);
    }
    
    // Method to check if manager can handle a project in the given period
    public boolean canHandleProjectInPeriod(Date openDate, Date closeDate) {
        for (Project project : managedProjects) {
            // Check for overlap using Date comparison
            if (!(closeDate.before(project.getApplicationOpenDate()) || 
                  openDate.after(project.getApplicationCloseDate()))) {
                return false;
            }
        }
        return true;
    }
    
    public void viewEnquiries() {
        System.out.println("Manager " + getName() + " is viewing enquiries...");
        // Implementation for viewing enquiries
    }
    
    public String getAccessLevel() {
        return "HIGH";
    }
    
    public void generateBookingReport() {
        System.out.println("Generating booking report for all projects managed by " + getName());
        // Implementation for report generation
    }
}
