/**
 * Class responsible for reading application data from a CSV file and converting it into a map of applications.
 * This class parses each line of the CSV to create `Application` objects.
 */

import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ApplicationFileReader extends FileReader<Application> {
    private Map<String, Project> projects;
    private Map<String, Applicant> applicants;

    /**
     * Constructor to initialize the ApplicationFileReader with project and applicant data.
     *
     * @param projects A map of all projects.
     * @param applicants A map of all applicants.
     */
    public ApplicationFileReader(Map<String, Project> projects, Map<String, Applicant> applicants) {
        super("Application.csv");
        this.projects = projects;
        this.applicants = applicants;
    }

    /**
     * Reads the application data from the CSV file and returns a map of applications.
     *
     * @return A map of applications where keys are application IDs and values are Application objects.
     */
    @Override
    public Map<String, Application> readFromFile() {
        return readCSV();
    }

    /**
     * Processes a single line of the CSV file and creates an Application object.
     *
     * @param line The line from the CSV file.
     * @param applications The map where the application will be added.
     */
    @Override
    protected void processLine(String line, Map<String, Application> applications) {
        try {
            String[] data = line.split(",");
            
            int applicationID = Integer.parseInt(data[0].trim());
            String applicantNRIC = data[1].trim();
            String projectName = data[2].trim();
            String flatTypeName = data[3].trim();
            String statusStr = data[4].trim();
            String dateAppliedStr = data[5].trim();
            
            Date dateApplied = new Date(); 
            if (!dateAppliedStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    dateApplied = sdf.parse(dateAppliedStr);
                } catch (ParseException e) {
                    System.out.println("Error parsing date applied: " + dateAppliedStr + ". Using current date.");
                }
            }
            
            Date dateBooked = null;
            if (data.length > 6 && !data[6].trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    dateBooked = sdf.parse(data[6].trim());
                } catch (ParseException e) {
                    System.out.println("Error parsing date booked: " + data[6]);
                }
            }

            
            Applicant applicant = applicants.get(applicantNRIC);
            Project project = projects.get(projectName);
            
            if (project == null || applicant == null) {
                return;
            }
            
            
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

            
            ApplicationStatus status = ApplicationStatus.valueOf(statusStr);
            
            
            Application application = new Application(
                applicationID, 
                applicant, 
                project, 
                flatType, 
                status, 
                dateApplied, 
                dateBooked
            );

            
            applications.put(String.valueOf(applicationID), application);
            
            
            applicant.setApplication(application);
            
        } catch (Exception e) {
            System.out.println("Error processing application line: " + line);
            e.printStackTrace();
        }
    }
}
