import java.util.Map;

public class ManagerFileReader extends FileReader<User> {
    
    public ManagerFileReader() {
        super("ManagerList.csv");
    }
    
    @Override
    public Map<String, User> readFromFile() {
        return readCSV();
    }
    
    @Override
    protected void processLine(String line, Map<String, User> users) {
        String[] data = line.split(",");
        HDBManager manager = new HDBManager();
        manager.setName(data[0].trim());
        manager.setNric(data[1].trim());
        manager.setAge(Integer.parseInt(data[2].trim()));
        manager.setMaritalStatus(data[3].trim());
        manager.setPassword(data[4].trim());
        users.put(manager.getNric(), manager);
    }

}
