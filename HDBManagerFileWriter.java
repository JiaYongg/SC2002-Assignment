import java.util.Map;

public class HDBManagerFileWriter extends FileWriter {
    
    public HDBManagerFileWriter() {
        super("ManagerList.csv");
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
        if (user instanceof Manager) {
            Manager manager = (Manager) user;
            return String.format("%s,%s,%d,%s,%s",
                manager.getName(),
                manager.getNric(),
                manager.getAge(),
                manager.getMaritalStatus(),
                manager.getPassword()
            );
        }
        return "";
    }
}
