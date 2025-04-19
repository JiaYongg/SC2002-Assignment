import java.util.HashMap;
import java.util.Map;

public class LoginController implements iAuthService {
    private Map<String, User> users = new HashMap<>();
    private User loggedInUser;
    private HDBManagerFileWriter HDBmanagerFileWriter;

    public LoginController() {
        // Initialize file writers
        HDBmanagerFileWriter = new HDBManagerFileWriter();
        
        // Load all users from files
        loadUsers();
    }

    private void loadUsers() {
        // Read managers
        ManagerFileReader managerReader = new ManagerFileReader();
        Map<String, User> managers = managerReader.readFromFile();
        managers.values().forEach(this::addUser);

        OfficerFileReader officerReader = new OfficerFileReader();
        Map<String, User> officers = officerReader.readFromFile();
        officers.values().forEach(this::addUser);

        // Read officers and applicants similarly
        // Read officers and applicants similarly
        // Read officers and applicants similarly
        // Read officers and applicants similarly
        // Read officers and applicants similarly
        // Read officers and applicants similarly

    }

    // In LoginController.java
    public Object getControllerForUser(User user) {
        if (user instanceof Manager) {
            return new HDBManagerController((Manager) user);
        // } else if (user instanceof HDBOfficer) {
        //     return new OfficerController((HDBOfficer) user);
        } else {
            return new ApplicantController((Applicant) user);
        }
    }
    

    // Add user to the system
    public void addUser(User user) {
        users.put(user.getNric(), user);
    }

    public boolean login(String nric, String password) {
        User user = users.get(nric);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user; // Store authenticated user
            return true;
        }
        return false;
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        if (loggedInUser == null) {
            System.out.println("No user is logged in. Please log in first.");
            return false;
        }

        if (!loggedInUser.getPassword().equals(oldPassword)) {
            System.out.println("Incorrect old password.");
            return false;
        }

        loggedInUser.setPassword(newPassword);
        System.out.println("Password changed successfully!");
        updateUserFile();
        return true;
    }

    public String getUserRole() {
        if (loggedInUser == null) {
            return null;
        }

        if (loggedInUser instanceof Manager) {
            return "MANAGER";
        } else if (loggedInUser instanceof HDBOfficer) {
            return "OFFICER";
        } else {
            return "APPLICANT";
        }
    }

    private void updateUserFile() {
        if (loggedInUser instanceof Manager) {
            // Filter only Manager users
            Map<String, User> managers = new HashMap<>();
            for (User user : users.values()) {
                if (user instanceof Manager) {
                    managers.put(user.getNric(), user);
                }
            }
            HDBmanagerFileWriter.writeToFile(managers);
        }
        // Add similar blocks for Officer and Applicant when implemented
    }

    /**
     * Gets the currently logged-in user
     * 
     * @return The logged-in user object or null if no user is logged in
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void logout() {
        loggedInUser = null;
    }

}
