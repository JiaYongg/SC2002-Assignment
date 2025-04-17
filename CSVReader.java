import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CSVReader {
    public static Map<String, User> readUsersFromCSV(String filePath) {
        Map<String, User> users = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                // Skip header row
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] data = line.split(","); // CSV fields
                User user = new User();
                user.setName(data[0].trim());  // Name
                user.setNric(data[1].trim());  // NRIC
                user.setAge(Integer.parseInt(data[2].trim()));  // Age
                user.setMaritalStatus(data[3].trim());  // Marital Status
                user.setPassword(data[4].trim());  // Password

                users.put(user.getName(), user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }
}