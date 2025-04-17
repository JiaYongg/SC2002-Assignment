import java.util.Scanner;

public class LoginView {
    private final LoginController loginController;
    private final Scanner scanner;
    private boolean exitRequested = false;
    

    public boolean isExitRequested() {
        return exitRequested;
    }

    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        this.scanner = new Scanner(System.in);
    }

    public boolean showLoginPrompt() {
        System.out.print("Enter UserID (NRIC): ");
        String enteredNric = scanner.nextLine().trim();

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
        
        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("\nPassword change failed. New passwords do not match.");
            return false;
        }
    
        boolean passwordChanged = loginController.changePassword(oldPassword, newPassword);
        if (passwordChanged) {
            System.out.println("\nPassword changed successfully! Please log in again.");
            return true; // Password was changed
        } else {
            System.out.println("\nPassword change failed. Please try again.");
            return false; // Password was not changed
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
                    return true; // Continue to role view
                case 2:
                    // If password was changed, return false to trigger re-login
                    if (changePasswordPrompt()) {
                        return false; // Force logout and re-login
                    } else {
                        // If password change failed, show menu again
                        return showIntermediateMenu();
                    }
                case 3:
                    return false; // Logout
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