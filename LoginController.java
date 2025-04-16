import java.util.HashMap;
import java.util.Map;

public class LoginController implements AuthService {
    private Map<String, User> users = new HashMap<>();

    // Add user to the system
    public void addUser(String username, User user) {
        users.put(username, user);
    }

    @Override
    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful! Welcome, " + username);
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            System.out.println("Password changed successfully!");
            return true;
        } else {
            System.out.println("Incorrect old password.");
            return false;
        }
    }
}