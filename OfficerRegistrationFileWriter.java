import java.util.Map;
import java.text.SimpleDateFormat;

public class OfficerRegistrationFileWriter extends FileWriter<OfficerRegistration> {
    public OfficerRegistrationFileWriter() {
        super("OfficerRegistration.csv");
    }

    @Override
    public void writeToFile(Map<String, OfficerRegistration> officerRegistrations) {
        writeCSV(officerRegistrations);
    }

    @Override
    protected String getHeader() {
        return "OfficerRegistrationID,Nric,ProjectName,Status,DateApplied";
    }

    @Override
    protected String formatLine(OfficerRegistration registration) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateAppliedStr = sdf.format(registration.getDateApplied());
        
        return String.format("%d,%s,%s,%s,%s",
                registration.getOfficerRegistrationID(),
                registration.getNric(),
                registration.getProject().getProjectName(),
                registration.getStatus().toString(),
                dateAppliedStr);
    }
}
