import java.util.HashMap;
import java.util.Map;

public class LoginController implements iAuthService {
    private Map<String, User> users = new HashMap<>();
    private User loggedInUser;
    private HDBManagerFileWriter managerFileWriter; 
    private HDBOfficerFileWriter officerFileWriter; 
    private ApplicantFileWriter applicantFileWriter;

    public LoginController() {
        
        managerFileWriter = new HDBManagerFileWriter();
        officerFileWriter = new HDBOfficerFileWriter();
        applicantFileWriter = new ApplicantFileWriter();

        
        loadUsers();
    }

    private void loadUsers() {
        
        HDBManagerFileReader managerReader = new HDBManagerFileReader();
        Map<String, User> managers = managerReader.readFromFile();
        managers.values().forEach(this::addUser);

        HDBOfficerFileReader officerReader = new HDBOfficerFileReader();
        Map<String, User> officers = officerReader.readFromFile();
        officers.values().forEach(this::addUser);

        ApplicantFileReader applicantReader = new ApplicantFileReader();
        Map<String, User> applicants = applicantReader.readFromFile();
        applicants.values().forEach(this::addUser);

    }

    public Object getControllerForUser(User user) {
        if (user instanceof HDBManager) {
            return new HDBManagerController((HDBManager) user);
        } else if (user instanceof HDBOfficer) {
            return new HDBOfficerController((HDBOfficer) user);
        } else {
            return new ApplicantController((Applicant) user);
        }
    }

    
    public void addUser(User user) {
        users.put(user.getNric(), user);
    }

    public boolean login(String nric, String password) {
        User user = users.get(nric);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user;
            return true;
        }
        return false;
    }

    public String getUserRole() {
        if (loggedInUser == null) {
            return null;
        }

        if (loggedInUser instanceof HDBManager) {
            return "MANAGER";
        } else if (loggedInUser instanceof HDBOfficer) {
            return "OFFICER";
        } else {
            return "APPLICANT";
        }
    }

    private void updateUserFile() {
        if (loggedInUser instanceof HDBManager) {
            
            Map<String, User> managers = new HashMap<>();
            for (User user : users.values()) {
                if (user instanceof HDBManager) {
                    managers.put(user.getNric(), user);
                }
            }
            managerFileWriter.writeToFile(managers);
        } else if (loggedInUser instanceof HDBOfficer) {
            
            Map<String, User> officers = new HashMap<>();
            for (User user : users.values()) {
                if (user instanceof HDBOfficer) {
                    officers.put(user.getNric(), user);
                }
            }
            officerFileWriter.writeToFile(officers);
        } else if (loggedInUser instanceof Applicant) {
            
            Map<String, User> applicants = new HashMap<>();
            for (User user : users.values()) {
                if (user instanceof Applicant && !(user instanceof HDBOfficer)) {
                    applicants.put(user.getNric(), user);
                }
            }
            applicantFileWriter.writeToFile(applicants);
        }

    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void logout() {
        loggedInUser = null;
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
}
