import java.util.HashMap;
import java.util.Map;

public class LoginController implements AuthService {
    private Map<String, User> users = new HashMap<>();
    private User loggedInUser;



    // Add user to the system
    public void addUser(User user) {
        users.put(user.getNric(), user);
    }


    // public boolean login(String nric, String password) {
    //     User user = users.get(nric);
    //     return user != null && user.getPassword().equals(password);
    // }
    public boolean login(String nric, String password) {
        User user = users.get(nric);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user; // Store authenticated user
            return true;
        }
        return false;
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
        return true;
    }



    // @Override
    // public boolean changePassword(String nric, String oldPassword, String newPassword) {
        
    //     User user = users.get(nric); // Use NRIC instead of username
    
    //     if (user != null && user.getPassword().equals(oldPassword)) {
    //         user.setPassword(newPassword);
    //         return true;
    //     } else {
    //         System.out.println("Incorrect old password.");
    //         return false;
    //     }
    // }
}


// import java.util.HashMap;
// import java.util.Map;

// public class LoginController implements AuthService {
//     private Map<String, User> users = new HashMap<>();
//     private User loggedInUser; // Track the currently logged-in user

//     public void addUser(User user) {
//         users.put(user.getNric(), user);
//     }

//     public boolean login(String nric, String password) {
//         User user = users.get(nric);
//         if (user != null && user.getPassword().equals(password)) {
//             loggedInUser = user; // Save authenticated user
//             return true;
//         }
//         return false;
//     }

//     @Override
//     public boolean changePassword(String oldPassword, String newPassword) {
//         if (loggedInUser == null) {
//             System.out.println("No user logged in. Please log in first.");
//             return false;
//         }

//         if (!loggedInUser.getPassword().equals(oldPassword)) {
//             System.out.println("Incorrect old password.");
//             return false;
//         }

//         loggedInUser.setPassword(newPassword);
//         System.out.println("Password changed successfully!");
//         return true;
//     }
// }