import java.util.regex.Pattern;

public class User {
    private static int idCounter = 1; // Static counter for unique IDs
    private int id;
    private int age;
    private String maritalStatus;
    private String name;
    private String password = "password"; // Default password

    private String nric;
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[SsTt]\\d{7}[A-Za-z]$");


    // Constructor assigns a unique ID
    public User() {
        this.id = idCounter++;
    }

    // public boolean login(String username, String password) {
    // }

    // public boolean changePassword(String oldPassword, String newPassword) {

    // }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }
    public String getMaritalStatus() {
        return maritalStatus;
    }
    public String getName(){
        return name;
    }
    public String getPassword() {
        return password;
    }



    public void setAge(int age) {
        if (age > 0) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Age must be positive.");
        }
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPassword(String password) {
        if (password.length() >= 8) { // Enforce minimum password length
            this.password = password;
        } else {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
    }
    public void setNric(String nric) {
        if (NRIC_PATTERN.matcher(nric).matches()) {
            this.nric = nric;
        } else {
            throw new IllegalArgumentException("Invalid NRIC format. Must match pattern: S/T 1234567 X");
        }
    }



}