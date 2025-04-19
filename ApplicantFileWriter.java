import java.util.Map;

public class ApplicantFileWriter extends FileWriter <User> {
    public ApplicantFileWriter() {
        super("ApplicantList.csv");
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
        if (user instanceof Applicant && !(user instanceof HDBOfficer)) {
            Applicant applicant = (Applicant) user;
            return String.format("%s,%s,%d,%s,%s",
                    applicant.getName(),
                    applicant.getNric(),
                    applicant.getAge(),
                    applicant.getMaritalStatus(),
                    applicant.getPassword()
            );
        }
        return "";
    }
}
