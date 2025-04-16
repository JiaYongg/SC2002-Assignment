public interface AuthService {
    boolean login(String username, String password);
    boolean changePassword(String username, String oldPassword, String newPassword);
}