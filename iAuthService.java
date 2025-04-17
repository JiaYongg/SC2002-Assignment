public interface iAuthService {
    boolean login(String username, String password);
    boolean changePassword(String oldPassword, String newPassword);
}