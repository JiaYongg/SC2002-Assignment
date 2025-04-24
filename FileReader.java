import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class FileReader<T> {
    protected String filePath;
    
    public FileReader(String filePath) {
        this.filePath = filePath;
    }
    
    
    public abstract Map<String, T> readFromFile();
    
    
    protected Map<String, T> readCSV() {
        Map<String, T> items = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                
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
    
    
    protected abstract void processLine(String line, Map<String, T> items);
}
