import java.util.Map;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class OfficerRegistrationFileWriter extends FileWriter<OfficerRegistration> {
    public OfficerRegistrationFileWriter() {
        super("OfficerRegistration.csv");
    }

    @Override
    public void writeToFile(Map<String, OfficerRegistration> officerRegistrations) {
        writeCSV(officerRegistrations);
    }

    public void appendRegistration(OfficerRegistration reg) {
    try (PrintWriter writer = new PrintWriter(new FileOutputStream("OfficerRegistration.csv", true))) {
        writer.println(formatLine(reg));
    } catch (Exception e) {
        System.out.println("Error appending to OfficerRegistration.csv");
        e.printStackTrace();
    }
    }

    @Override
    protected String getHeader() {
        return "OfficerRegistrationID,Nric,ProjectName,Status,DateApplied";
    }

    @Override
    protected String formatLine(OfficerRegistration registration) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateAppliedStr = sdf.format(registration.getRegistrationDate());
        
        return String.format("%d,%s,%s,%s,%s",
                registration.getRegistrationId(),
                registration.getOfficer().getNric(),
                registration.getProject().getProjectName(),
                registration.getRegistrationStatus().toString(),
                dateAppliedStr);
    }
}
