import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        String[] data = line.split(",", -1);

        if (data.length < 14) {
            System.out.println("Skipping malformed line: " + line);
            return;
        }

        try {
            
            String projectName = data[0].trim();
            String neighborhood = data[1].trim();

            List<FlatType> flatTypes = new ArrayList<>();

            
            if (!data[2].isEmpty()) {
                flatTypes.add(new FlatType(data[2].trim(), Integer.parseInt(data[3].trim()), Double.parseDouble(data[4].trim())));
            }

            
            if (!data[5].isEmpty()) {
                flatTypes.add(new FlatType(data[5].trim(), Integer.parseInt(data[6].trim()), Double.parseDouble(data[7].trim())));
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date openDate = dateFormat.parse(data[8].trim());
            Date closeDate = dateFormat.parse(data[9].trim());

            String managerName = data[10].trim();
            HDBManager manager = findOrCreateManager(managerName);

            int officerSlots = Integer.parseInt(data[11].trim());
            boolean isVisible = Boolean.parseBoolean(data[13].trim());

            Project project = new Project(projectName, neighborhood, openDate, closeDate, isVisible, flatTypes, manager, officerSlots);

            if (!data[12].isEmpty()) {
                String[] officerNames = data[12].split(";");
                for (String name : officerNames) {
                    HDBOfficer officer = findOrCreateOfficer(name.trim());
                    officer.setAssignedProject(project);
                    project.assignOfficer(officer);
                }
            }

            
            new ProjectController().updateProjectVisibility(project);

            projects.put(projectName, project);

        } catch (Exception e) {
            System.out.println("Error processing line: " + line);
            e.printStackTrace();
        }
    }

    private HDBOfficer findOrCreateOfficer(String name) {
        HDBOfficer officer = new HDBOfficer();
        officer.setName(name);
        try {
            
            officer.setNric("T" + (int)(Math.random() * 9000000 + 1000000) + "Z");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid NRIC generated for officer: " + name);
        }
        return officer;
    }

    
    
    private HDBManager findOrCreateManager(String managerName) {
        
        
        HDBManager manager = new HDBManager();
        manager.setName(managerName);
        
        
        try {
            manager.setNric("S1234567A");
        } catch (IllegalArgumentException e) {
            System.out.println("Warning: Could not set default NRIC for manager: " + managerName);
        }
        
        return manager;
    }
}
