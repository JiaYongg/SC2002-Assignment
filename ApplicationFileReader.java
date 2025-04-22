import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ApplicationFileReader extends FileReader<Application> {
    private Map<String, Project> projects;
    private Map<String, Applicant> applicants;

    public ApplicationFileReader(Map<String, Project> projects, Map<String, Applicant> applicants) {
        super("Application.csv");
        this.projects = projects;
        this.applicants = applicants;
    }

    @Override
    public Map<String, Application> readFromFile() {
        return readCSV();
    }

    @Override
    protected void processLine(String line, Map<String, Application> applications) {
        try {
            String[] data = line.split(",");
            
            if (data.length < 4) {
                System.out.println("Invalid application data format: " + line);
                return;
            }
            
            int applicationID = Integer.parseInt(data[0].trim());
            String applicantNRIC = data[1].trim();
            String projectName = data[2].trim();
            String flatTypeName = data[3].trim();
            String statusStr = data[4].trim();
            String dateBookedStr = data.length > 5 ? data[5].trim() : "";

            // Look up the applicant, project, and flat type
            Applicant applicant = applicants.get(applicantNRIC);
            Project project = projects.get(projectName);
            
            if (project == null || applicant == null) {
                
                return;
            }
            
            // Find the flat type within the project
            FlatType flatType = null;
            for (FlatType ft : project.getFlatTypes()) {
                if (ft.getName().equals(flatTypeName)) {
                    flatType = ft;
                    break;
                }
            }
            
            if (flatType == null) {
                System.out.println("Could not find flat type for application: " + line);
                return;
            }

            // Parse the application status
            ApplicationStatus status = ApplicationStatus.valueOf(statusStr);
            
            // Create the application with the specified ID
            Application application = new Application(applicationID, applicant, project, flatType);
            application.setStatus(status);

            // Set the date booked if available
            if (!dateBookedStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dateBooked = sdf.parse(dateBookedStr);
                application.setDateBooked(dateBooked);
            }

            // Add to the map of applications
            applications.put(String.valueOf(applicationID), application);
            
            // Associate the application with the applicant
            applicant.setApplication(application);
            
        } catch (Exception e) {
            System.out.println("Error processing application line: " + line);
            e.printStackTrace();
        }
    }
}
