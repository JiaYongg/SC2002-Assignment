import java.util.Scanner;

public class LoginView {
    private final LoginController loginController;
    private final Scanner scanner;

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

    public void changePasswordFlow() {

        System.out.print("Enter your current password: ");
        String oldPassword = scanner.nextLine().trim();

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();

        boolean passwordChanged = loginController.changePassword(oldPassword, newPassword);
        if (passwordChanged) {
            System.out.println("\nPassword changed successfully! Please log in again.\n");

            // 🔄 Re-prompt login after password change
            boolean isAuthenticated = false;
            while (!isAuthenticated) {
                isAuthenticated = showLoginPrompt();
            }
        } else {
            System.out.println("\nPassword change failed. Please try again.");
        }
    }

    public void closeScanner() {
        scanner.close();
    }
}