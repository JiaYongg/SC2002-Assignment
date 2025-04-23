import java.util.Map;
import java.util.Date;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;


public class OfficerRegistrationFileReader extends FileReader<OfficerRegistration> {
    private Map<String, Project> projects;
    private Map<String, HDBOfficer> officers;

    public OfficerRegistrationFileReader(Map<String, Project> projects, Map<String, HDBOfficer> officers) {
        super("OfficerRegistration.csv");
        this.projects = projects;
        this.officers = officers;
    }

    @Override
    public Map<String, OfficerRegistration> readFromFile() {
        return readCSV();
    }

    public static int getLastUsedRegistrationId(String filename) {
    int lastId = 0;
    try (BufferedReader br = new BufferedReader(new java.io.FileReader(filename))) {
        String line;
        br.readLine(); // skip header
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 1) {
                int id = Integer.parseInt(parts[0].trim());
                if (id > lastId) {
                    lastId = id;
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error reading OfficerRegistration.csv for ID tracking.");
    }
    return lastId;
}

    @Override
    protected void processLine(String line, Map<String, OfficerRegistration> officerRegistrations) {
        try {
            String[] data = line.split(",");
            
            if (data.length < 5) {
                System.out.println("Invalid officer registration data format: " + line);
                return;
            }
            
            int officerRegistrationID = Integer.parseInt(data[0].trim());
            String nric = data[1].trim();
            String projectName = data[2].trim();
            String statusStr = data[3].trim();
            String dateAppliedStr = data[4].trim();

            // Look up the project
            Project project = projects.get(projectName);
            
            if (project == null) {
                System.out.println("Could not find project for officer registration: " + line);
                return;
            }
            
            // Parse the officer registration status
            OfficerRegistrationStatus status = OfficerRegistrationStatus.valueOf(statusStr);
            
            // Parse the date applied
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateApplied = sdf.parse(dateAppliedStr);

            HDBOfficer officer = officers.get(nric);

            // Create the officer registration with the specified ID
            OfficerRegistration registration = new OfficerRegistration(officerRegistrationID, officer, project, status, dateApplied);

            // Add to the map of officer registrations
            officerRegistrations.put(String.valueOf(officerRegistrationID), registration);
            
        } catch (Exception e) {
            System.out.println("Error processing officer registration line: " + line);
            e.printStackTrace();
        }
    }
}
