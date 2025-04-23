import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

public class ApplicationFileWriter extends FileWriter<Application> {
    
    public ApplicationFileWriter() {
        super("Application.csv");
    }

    public void updateApplication(Application updatedApp) {
        ApplicantFileReader applicantReader = new ApplicantFileReader();
        Map<String, User> userMap = applicantReader.readFromFile();

        Map<String, Applicant> applicantMap = new HashMap<>();
        for (User user : userMap.values()) {
            if (user instanceof Applicant) {
                applicantMap.put(user.getNric(), (Applicant) user);
            }
        }

        ProjectFileReader projectReader = new ProjectFileReader();
        Map<String, Project> projectMap = projectReader.readFromFile();

        ApplicationFileReader reader = new ApplicationFileReader(projectMap, applicantMap);
        Map<String, Application> allApps = reader.readFromFile();

        allApps.put(String.valueOf(updatedApp.getApplicationID()), updatedApp);
        writeToFile(allApps);
    }
    
    @Override
    public void writeToFile(Map<String, Application> applications) {
        writeCSV(applications);
    }
    
    @Override
    protected String getHeader() {
        return "ApplicationID,ApplicantNRIC,ProjectName,FlatType,Status,DateApplied,DateBooked";
    }
    
    @Override
    protected String formatLine(Application application) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateAppliedStr = application.getDateApplied() != null ? sdf.format(application.getDateApplied()) : "";
        String dateBookedStr = application.getDateBooked() != null ? sdf.format(application.getDateBooked()) : "";
        
        return String.format("%d,%s,%s,%s,%s,%s,%s",
            application.getApplicationID(),
            application.getApplicant().getNric(),
            application.getProject().getProjectName(),
            application.getFlatType().getName(),
            application.getStatus().toString(),
            dateAppliedStr,
            dateBookedStr);
    }
}
