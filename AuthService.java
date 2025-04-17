public interface AuthService {
    boolean login(String username, String password);
    boolean changePassword(String oldPassword, String newPassword);
}