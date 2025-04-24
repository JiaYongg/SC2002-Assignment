import java.util.Scanner;

public class LoginView {
    private final LoginController loginController;
    private final Scanner scanner;


    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        this.scanner = new Scanner(System.in);
    }

    private boolean isValidNric(String nric) {
        String regex = "^[ST]\\d{7}[A-Z]$";
        return nric.matches(regex);
    }


    public boolean showLoginPrompt() {
        System.out.print("Enter UserID (NRIC): ");
        String enteredNric = scanner.nextLine().trim();

        if (!isValidNric(enteredNric)) {
            System.out.println("Invalid NRIC format. Please enter a valid NRIC.");
            return false;
        }

        System.out.print("Enter Password: ");
        String enteredPassword = scanner.nextLine().trim();

        boolean isAuthenticated = loginController.login(enteredNric, enteredPassword);

        if (isAuthenticated) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid NRIC or Password. Please try again.");
        }

        return isAuthenticated;
    }



    public boolean changePasswordPrompt() {
        System.out.print("Enter your current password: ");
        String oldPassword = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();
        
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("\nPassword change failed. New passwords do not match.");
            return false;
        }
    
        boolean passwordChanged = loginController.changePassword(oldPassword, newPassword);
        if (passwordChanged) {
            System.out.println("\nPassword changed successfully! Please log in again.");
            return true; 
        } else {
            System.out.println("\nPassword change failed. Please try again.");
            return false; 
        }
    }
    


    public boolean showIntermediateMenu() {
        User currentUser = loginController.getLoggedInUser();
        String userRole = loginController.getUserRole();
    
        System.out.println("\n===== Welcome, " + currentUser.getName() + " =====");
        System.out.println("You are logged in as: " + userRole);
        System.out.println("\nPlease select an option:");
        System.out.println("1. Continue to " + userRole + " Interface");
        System.out.println("2. Change Password");
        System.out.println("3. Logout");
        System.out.print("Enter your choice: ");
    
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    return true; 
                case 2:
                    
                    if (changePasswordPrompt()) {
                        return false; 
                    } else {
                        
                        return showIntermediateMenu();
                    }
                case 3:
                    return false; 
                default:
                    System.out.println("Invalid option. Continuing to " + userRole + " Interface.");
                    return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Continuing to " + userRole + " Interface.");
            return true;
        }
    }
    


    public void closeScanner() {
        scanner.close();
    }
}