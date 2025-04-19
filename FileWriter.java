import java.io.*;
import java.util.Map;

public abstract class FileWriter <T>{
    protected String filePath;
    
    public FileWriter(String filePath) {
        this.filePath = filePath;
    }
    
    // Abstract method to be implemented by child classes
    public abstract void writeToFile(Map<String, T> items);
    
    // Common method to write CSV files
    protected void writeCSV(Map<String, T> items) {
        try (PrintWriter writer = new PrintWriter(new java.io.FileWriter(filePath))) {
            // Write header
            writer.println(getHeader());
            
            // Write user data
            for (T item : items.values()) {
                writer.println(formatLine(item));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
    
    // Abstract methods for header and line formatting
    protected abstract String getHeader();
    protected abstract String formatLine(T item);
}
