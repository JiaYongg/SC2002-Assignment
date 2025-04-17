import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LoginController loginController = new LoginController();
        readAndAddUsersFromFile(loginController);
        LoginView loginView = new LoginView(loginController);

        // Loop login until successful
        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            isAuthenticated = loginView.showLoginPrompt();
        }

        // Show menu and handle user actions
        loginView.showMenu();
        
        // Close resources
        loginView.closeScanner();
    }

    public static void readAndAddUsersFromFile(LoginController loginController) {
        String filePath = "ApplicantList.csv"; // CSV file name
        // Read users from CSV file
        Map<String, User> users = CSVReader.readUsersFromCSV(filePath);
        // Add users to login controller
        users.values().forEach(loginController::addUser);
    }
    
}
