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

        String userRole = loginController.getUserRole();
        User currentUser = loginController.getLoggedInUser();
        
        switch (userRole) {
            case "MANAGER":
                Manager manager = (Manager) currentUser;
                ManagerController managerController = new ManagerController(manager);
                ManagerView managerView = new ManagerView(managerController);
                managerView.displayManagerMenu();
                break;
                
            // case "OFFICER":
            //     Officer officer = (Officer) currentUser;
            //     OfficerController officerController = new OfficerController(officer);
            //     OfficerView officerView = new OfficerView(officerController);
            //     officerView.displayOfficerMenu();
            //     break;
                
            // case "APPLICANT":
            // default:
            //     ApplicantController applicantController = new ApplicantController(currentUser);
            //     ApplicantView applicantView = new ApplicantView(applicantController);
            //     applicantView.displayApplicantMenu();
            //     break;
        }
        
        // Close resources
        loginView.closeScanner();
    }

    

    public static void readAndAddUsersFromFile(LoginController loginController) {
        // Read applicants
        ApplicantFileReader applicantReader = new ApplicantFileReader();
        Map<String, User> applicants = applicantReader.readFromFile();
        applicants.values().forEach(loginController::addUser);
        
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
