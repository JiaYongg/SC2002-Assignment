import java.io.*;
import java.util.Map;

public abstract class FileWriter <T>{
    protected String filePath;
    
    public FileWriter(String filePath) {
        this.filePath = filePath;
    }
    
    
    public abstract void writeToFile(Map<String, T> items);
    
    
    protected void writeCSV(Map<String, T> items) {
        try (PrintWriter writer = new PrintWriter(new java.io.FileWriter(filePath))) {
            
            writer.println(getHeader());
            
            
            for (T item : items.values()) {
                writer.println(formatLine(item));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
    
    
    protected abstract String getHeader();
    protected abstract String formatLine(T item);
}
