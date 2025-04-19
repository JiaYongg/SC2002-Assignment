import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectFileReader extends FileReader<Project> {

    public ProjectFileReader() {
        super("ProjectList.csv");
    }

    @Override
    public Map<String, Project> readFromFile() {
        return readCSV();
    }

    @Override
    protected void processLine(String line, Map<String, Project> projects) {
        String[] data = line.split(",");
        
        // Skip if line doesn't have enough fields
        if (data.length < 12) {
            System.out.println("Warning: Skipping malformed line in CSV: " + line);
            return;
        }
        
        try {
            // Parse project details
            String projectName = data[0].trim();
            String neighborhood = data[1].trim();
            
            // Parse flat types
            List<FlatType> flatTypes = new ArrayList<>();
            
            // Add Type 1 if it exists
            if (!data[2].isEmpty()) {
                String typeName = data[2].trim();
                int units = Integer.parseInt(data[3].trim());
                double price = Double.parseDouble(data[4].trim());
                flatTypes.add(new FlatType(typeName, units, price));
            }
            
            // Add Type 2 if it exists
            if (data.length > 5 && !data[5].isEmpty()) {
                String typeName = data[5].trim();
                int units = Integer.parseInt(data[6].trim());
                double price = Double.parseDouble(data[7].trim());
                flatTypes.add(new FlatType(typeName, units, price));
            }
            
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date openDate = dateFormat.parse(data[8].trim());
            Date closeDate = dateFormat.parse(data[9].trim());
            
            // Parse manager
            String managerName = data[10].trim();
            Manager manager = findOrCreateManager(managerName);
            
            // Parse officer slots
            int officerSlots = Integer.parseInt(data[11].trim());
            
            // Create project
            Project project = new Project(
                projectName,
                neighborhood,
                openDate,
                closeDate,
                true, // Default visibility to true for loaded projects
                flatTypes,
                manager,
                officerSlots
            );
            
            // Add project to the map with project name as key
            projects.put(projectName, project);
            
        } catch (NumberFormatException | ParseException e) {
            System.out.println("Error processing line: " + line);
            System.out.println("Error details: " + e.getMessage());
        }
    }
    
    
    // Helper method to find or create a Manager object
    private Manager findOrCreateManager(String managerName) {
        // In a real implementation, you would look up the manager in a repository
        // For now, create a placeholder manager with a valid NRIC
        Manager manager = new Manager();
        manager.setName(managerName);
        
        // Set a valid NRIC format to avoid validation errors
        try {
            manager.setNric("S1234567A");
        } catch (IllegalArgumentException e) {
            System.out.println("Warning: Could not set default NRIC for manager: " + managerName);
        }
        
        return manager;
    }
}
