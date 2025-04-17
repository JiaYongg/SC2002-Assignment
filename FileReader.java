import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class FileReader {
    protected String filePath;
    
    public FileReader(String filePath) {
        this.filePath = filePath;
    }
    
    // Abstract method to be implemented by child classes
    public abstract Map<String, User> readFromFile();
    
    // Common method to read CSV files
    protected Map<String, User> readCSV() {
        Map<String, User> users = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                // Skip header row
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                
                processLine(line, users);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    // Abstract method for processing each line based on file type
    protected abstract void processLine(String line, Map<String, User> users);
}
