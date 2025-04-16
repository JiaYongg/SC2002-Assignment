public class Main {
    public static void main(String[] args) {
        // Initialize authentication service
        AuthService authService = new LoginController();

        // Create and add users
        User alice = new User();
        alice.setName("Alice");
        alice.setAge(25);
        alice.setMaritalStatus("Single");
        alice.setNric("t1234567m");

        User bob = new User();
        bob.setName("Bob");

        ((LoginController) authService).addUser("Alice", alice);
        ((LoginController) authService).addUser("Bob", bob);

        // Attempt login
        authService.login("Alice", "password");

        // Change password
        authService.changePassword("Alice", "password", "newAlicePass");

        System.out.println(alice.getPassword());
    }
}