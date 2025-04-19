import java.util.Map;

public class HDBOfficerFileWriter extends FileWriter<User> {
    
    public HDBOfficerFileWriter() {
        super("OfficerList.csv");
    }
    
    @Override
    public void writeToFile(Map<String, User> users) {
        writeCSV(users);
    }
    
    @Override
    protected String getHeader() {
        return "Name,NRIC,Age,Marital Status,Password";
    }
    
    @Override
    protected String formatLine(User user) {
        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
            return String.format("%s,%s,%d,%s,%s",
                officer.getName(),
                officer.getNric(),
                officer.getAge(),
                officer.getMaritalStatus(),
                officer.getPassword()
            );
        }
        return "";
    }
}
