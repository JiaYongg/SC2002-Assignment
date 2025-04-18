import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LoginController loginController = new LoginController();
        readAndAddUsersFromFile(loginController);
        LoginView loginView = new LoginView(loginController);
        
        boolean programRunning = true;
        
        while (programRunning) {
            // Reset authentication status for each login attempt
            boolean isAuthenticated = false;
            
            // Loop login until successful
            while (!isAuthenticated) {
                isAuthenticated = loginView.showLoginPrompt();
                
            }
            
            boolean continueToRoleView = loginView.showIntermediateMenu();

            if (!continueToRoleView) {
                // User chose to logout after intermediate menu or changed password
                loginController.logout();
                System.out.println("Logged out successfully. Returning to login screen...");
                continue; // Skip to the next iteration of the outer loop
            }
            
            String userRole = loginController.getUserRole();
            User currentUser = loginController.getLoggedInUser();
            boolean exitProgram = false;
            
            // Process based on user role
            switch (userRole) {
                case "MANAGER":
                    Manager manager = (Manager) currentUser;
                    HDBManagerController HDBmanagerController = new HDBManagerController(manager);
                    HDBManagerView HDBmanagerView = new HDBManagerView(HDBmanagerController);
                    exitProgram = HDBmanagerView.displayManagerMenu();
                    break;
                    
                // case "OFFICER":
                //     Officer officer = (Officer) currentUser;
                //     OfficerController officerController = new OfficerController(officer);
                //     OfficerView officerView = new OfficerView(officerController);
                //     exitProgram = officerView.displayOfficerMenu();
                //     break;
                    
                // case "APPLICANT":
                // default:
                //     ApplicantController applicantController = new ApplicantController(currentUser);
                //     ApplicantView applicantView = new ApplicantView(applicantController);
                //     exitProgram = applicantView.displayApplicantMenu();
                //     break;
            }
            
            // If user logged out (not staying logged in), reset the login controller
            if (exitProgram) {
                // User chose to logout, reset the login controller
                loginController.logout();
                System.out.println("Logged out successfully. Returning to login screen...");
            } else {
                // User chose to exit the program
                programRunning = false;
            }
            
        }
        
        // Close resources
        loginView.closeScanner();
        System.out.println("Program terminated. Goodbye!");
    }

    public static void readAndAddUsersFromFile(LoginController loginController) {
        // Read applicants
        // ApplicantFileReader applicantReader = new ApplicantFileReader();
        // Map<String, User> applicants = applicantReader.readFromFile();
        // applicants.values().forEach(loginController::addUser);
        
        // Read managers
        ManagerFileReader managerReader = new ManagerFileReader();
        Map<String, User> managers = managerReader.readFromFile();
        managers.values().forEach(loginController::addUser);
        
        // Read officers
        // OfficerFileReader officerReader = new OfficerFileReader();
        // Map<String, User> officers = officerReader.readFromFile();
        // officers.values().forEach(loginController::addUser);
    }
}
