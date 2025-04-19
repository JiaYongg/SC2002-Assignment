import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class FileReader<T> {
    protected String filePath;
    
    public FileReader(String filePath) {
        this.filePath = filePath;
    }
    
    // Abstract method to be implemented by child classes
    public abstract Map<String, T> readFromFile();
    
    // Common method to read CSV files
    protected Map<String, T> readCSV() {
        Map<String, T> items = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                // Skip header row
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                
                processLine(line, items);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    // Abstract method for processing each line based on file type
    protected abstract void processLine(String line, Map<String, T> items);
}
