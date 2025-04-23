import java.util.HashMap;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class OfficerRegistrationFileWriter extends FileWriter<OfficerRegistration> {
    public OfficerRegistrationFileWriter() {
        super("OfficerRegistration.csv");
    }

    public void updateRegistration(OfficerRegistration updated) {
        ProjectFileReader projectReader = new ProjectFileReader();
        Map<String, Project> projectMap = projectReader.readFromFile();

        HDBOfficerFileReader officerReader = new HDBOfficerFileReader();
        Map<String, User> userMap = officerReader.readFromFile();

        Map<String, HDBOfficer> officerMap = new HashMap<>();
        for (User u : userMap.values()) {
            if (u instanceof HDBOfficer o) {
                officerMap.put(o.getNric(), o);
            }
        }

        OfficerRegistrationFileReader reader = new OfficerRegistrationFileReader(projectMap, officerMap);
        Map<String, OfficerRegistration> allRegs = reader.readFromFile();

        allRegs.put(String.valueOf(updated.getRegistrationId()), updated); // Make sure getId() exists in your class
        writeToFile(allRegs);
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
