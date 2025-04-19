import java.util.Map;

public class ApplicantFileReader extends FileReader<User> {
    
    public ApplicantFileReader() {
        super("ApplicantList.csv");
    }
    
    @Override
    public Map<String, User> readFromFile() {
        return readCSV();
    }
    
    @Override
    protected void processLine(String line, Map<String, User> users) {
        String[] data = line.split(",");
        User applicant = new User();
        applicant.setName(data[0].trim());
        applicant.setNric(data[1].trim());
        applicant.setAge(Integer.parseInt(data[2].trim()));
        applicant.setMaritalStatus(data[3].trim());
        applicant.setPassword(data[4].trim());
        users.put(applicant.getNric(), applicant);
    }
}
