import java.util.Scanner;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LoginController loginController = new LoginController(); // Create LoginController
        
        readAndAddUsersFromFile(loginController); // Pass it to function

        // Initialize UI
        LoginUI loginUI = new LoginUI(loginController);
        Scanner scanner = new Scanner(System.in);

        // Loop login until successful
        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            isAuthenticated = loginUI.showLoginPrompt();
        }

        // Handle user actions
        while (true) {
            printMenu();

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 9:
                    loginUI.changePasswordFlow();
                    break;
                case 0:
                    System.out.println("Exiting program... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }


















    public static void readAndAddUsersFromFile(LoginController loginController) {
        String filePath = "ApplicantList.csv"; // CSV file name

        // Read users from CSV file
        Map<String, User> users = CSVReader.readUsersFromCSV(filePath);

        // Add users to login controller
        users.values().forEach(loginController::addUser);
    }

    public static void printMenu(){
        System.out.println("\nOptions:");
        System.out.println("9 - Change Password");
        System.out.println("0 - Quit");
        System.out.print("Enter your choice: ");
    }
}