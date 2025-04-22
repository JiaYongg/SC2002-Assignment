public class Main {
    public static void main(String[] args) {
        LoginController loginController = new LoginController();
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
                    HDBManager manager = (HDBManager) currentUser;
                    HDBManagerController HDBmanagerController = new HDBManagerController(manager);
                    HDBManagerView HDBmanagerView = new HDBManagerView(HDBmanagerController);
                    exitProgram = HDBmanagerView.displayManagerMenu();
                    break;

                // case "OFFICER":
                //     HDBOfficer officer = (HDBOfficer) currentUser;
                //     HDBOfficerController officerController = new HDBOfficerController(officer);
                //     HDBOfficerView officerView = new HDBOfficerView(officerController);
                //     exitProgram = officerView.displayOfficerMenu();
                //     break;

                case "APPLICANT":
                default:
                    Applicant applicant = (Applicant) currentUser;
                    ApplicantController applicantController = new ApplicantController(applicant);
                    ApplicantView applicantView = new ApplicantView(applicantController);
                    exitProgram = applicantView.displayApplicantMenu();
                    break;
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

}
