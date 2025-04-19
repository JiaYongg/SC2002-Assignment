import java.util.Map;

public class OfficerFileReader extends FileReader<User> {
    
    public OfficerFileReader() {
        super("OfficerList.csv");
    }
    
    @Override
    public Map<String, User> readFromFile() {
        return readCSV();
    }
    
    @Override
    protected void processLine(String line, Map<String, User> users) {
        String[] data = line.split(",");
        HDBOfficer officer = new HDBOfficer();
        officer.setName(data[0].trim());
        officer.setNric(data[1].trim());
        officer.setAge(Integer.parseInt(data[2].trim()));
        officer.setMaritalStatus(data[3].trim());
        officer.setPassword(data[4].trim());
        users.put(officer.getNric(), officer);
    }

}