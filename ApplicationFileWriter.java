import java.util.Map;
import java.text.SimpleDateFormat;

public class ApplicationFileWriter extends FileWriter<Application> {
    
    public ApplicationFileWriter() {
        super("Application.csv");
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
