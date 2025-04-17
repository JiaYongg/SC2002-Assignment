import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class ManagerController {
    private Manager currentManager;
    // private List<Project> allProjects;
    
    public ManagerController(Manager manager) {
        this.currentManager = manager;
        // this.allProjects = loadProjects();
    } 
    
    // // Project management methods
    // public Project createProject(String name, String neighborhood, List<FlatType> flatTypes, 
    //                            LocalDate openDate, LocalDate closeDate, int officerSlots) {
    //     Project project = new Project(name, neighborhood, flatTypes, openDate, closeDate, officerSlots);
    //     project.setManager(currentManager);
    //     allProjects.add(project);
    //     currentManager.addManagedProject(project);
    //     saveProjects();
    //     return project;
    // }
    
    // public void editProject(Project project, String newName, String newNeighborhood, 
    //                       List<FlatType> newFlatTypes, LocalDate newOpenDate, 
    //                       LocalDate newCloseDate, int newOfficerSlots) {
    //     if (!currentManager.getManagedProjects().contains(project)) {
    //         throw new IllegalArgumentException("Manager does not manage this project");
    //     }
        
    //     project.setName(newName);
    //     project.setNeighborhood(newNeighborhood);
    //     project.setFlatTypes(newFlatTypes);
    //     project.setOpenDate(newOpenDate);
    //     project.setCloseDate(newCloseDate);
    //     project.setOfficerSlots(newOfficerSlots);
        
    //     saveProjects();
    // }
    
    // public void deleteProject(Project project) {
    //     if (!currentManager.getManagedProjects().contains(project)) {
    //         throw new IllegalArgumentException("Manager does not manage this project");
    //     }
        
    //     allProjects.remove(project);
    //     currentManager.removeManagedProject(project);
        
    //     saveProjects();
    // }
    
    // public void toggleVisibility(Project project) {
    //     if (!currentManager.getManagedProjects().contains(project)) {
    //         throw new IllegalArgumentException("Manager does not manage this project");
    //     }
        
    //     project.setVisible(!project.isVisible());
        
    //     saveProjects();
    // }
    
    // public List<Project> viewAllProjects() {
    //     return new ArrayList<>(allProjects);
    // }
    
    // public List<Project> viewOwnProjects() {
    //     return currentManager.getManagedProjects();
    // }
    
    // // Load projects from CSV file
    // private List<Project> loadProjects() {
    //     List<Project> projects = new ArrayList<>();
    //     String filePath = "ProjectList.csv";
        
    //     try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
    //         String line;
    //         boolean headerSkipped = false;
            
    //         while ((line = br.readLine()) != null) {
    //             if (!headerSkipped) {
    //                 headerSkipped = true;
    //                 continue;
    //             }
                
    //             String[] data = line.split(",");
    //             if (data.length < 12) {
    //                 System.out.println("Warning: Skipping malformed line in CSV: " + line);
    //                 continue;
    //             }
                
    //             // Parse project data
    //             Project project = parseProjectFromCSV(data);
    //             projects.add(project);
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Error reading ProjectList.csv: " + e.getMessage());
    //     }
        
    //     return projects;
    // }
    
    // // Parse project from CSV data
    // private Project parseProjectFromCSV(String[] data) {
    //     // Implementation details for parsing project data
    //     // This would extract all the fields from the CSV line
    //     // and create a Project object
        
    //     return new Project(); // Placeholder
    // }
    
    // // Save projects to CSV file
    // private void saveProjects() {
    //     String filePath = "ProjectList.csv";
        
    //     try (FileWriter writer = new FileWriter(filePath)) {
    //         // Write header
    //         writer.write("Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1," +
    //                    "Type 2,Number of units for Type 2,Selling price for Type 2," +
    //                    "Application opening date,Application closing date,Manager,Officer Slot,Officer\n");
            
    //         // Write project data
    //         for (Project project : allProjects) {
    //             writer.write(formatProjectToCSV(project) + "\n");
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Error saving projects to CSV: " + e.getMessage());
    //     }
    // }
    
    // // Format project to CSV line
    // private String formatProjectToCSV(Project project) {
    //     // Implementation details for formatting project data
    //     // This would convert a Project object to a CSV line
        
    //     return ""; // Placeholder
    // }
    
    // // Other methods for handling applications, officer registrations, etc.
    // public void approveOfficerRegistration(OfficerRegistration request) {
    //     // Implementation
    // }
    
    // public void rejectOfficerRegistration(OfficerRegistration request) {
    //     // Implementation
    // }
    
    // public void approveApplication(Application application) {
    //     // Implementation
    // }
    
    // public void rejectApplication(Application application) {
    //     // Implementation
    // }
    
    // public void approveWithdrawal(WithdrawalRequest request) {
    //     // Implementation
    // }
    
    // public void rejectWithdrawal(WithdrawalRequest request) {
    //     // Implementation
    // }
    
    // public void generateBookingReport() {
    //     // Implementation
    // }
}
