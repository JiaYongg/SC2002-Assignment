public class Main {
    public static void main(String[] args) {
        LoginController loginController = new LoginController();
        LoginView loginView = new LoginView(loginController);

        boolean programRunning = true;

        while (programRunning) {
            
            boolean isAuthenticated = false;

            
            while (!isAuthenticated) {
                isAuthenticated = loginView.showLoginPrompt();

            }

            boolean continueToRoleView = loginView.showIntermediateMenu();

            if (!continueToRoleView) {
                
                loginController.logout();
                System.out.println("Logged out successfully. Returning to login screen...");
                continue; 
            }

            String userRole = loginController.getUserRole();
            User currentUser = loginController.getLoggedInUser();

            boolean exitProgram = false;

            
            switch (userRole) {
                case "MANAGER":
                    HDBManager manager = (HDBManager) currentUser;
                    HDBManagerController HDBmanagerController = new HDBManagerController(manager);
                    HDBManagerView HDBmanagerView = new HDBManagerView(HDBmanagerController);
                    exitProgram = HDBmanagerView.displayManagerMenu();
                    break;

                 case "OFFICER":
                    HDBOfficer officer = (HDBOfficer) currentUser;
                    HDBOfficerController officerController = new HDBOfficerController(officer);
                    HDBOfficerView officerView = new HDBOfficerView(officerController, officer);
                    exitProgram = officerView.displayOfficerMenu();
                    break;

               case "APPLICANT":
               default:
                   Applicant applicant = (Applicant) currentUser;
                   ApplicantController applicantController = new ApplicantController(applicant);
                   ApplicantView applicantView = new ApplicantView(applicantController);
                   exitProgram = applicantView.displayApplicantMenu();
                   break;
            }

            
            if (exitProgram) {
                
                loginController.logout();
                System.out.println("Logged out successfully. Returning to login screen...");
            } else {
                
                programRunning = false;
            }

        }

        
        loginView.closeScanner();
        System.out.println("Program terminated. Goodbye!");
    }

}
