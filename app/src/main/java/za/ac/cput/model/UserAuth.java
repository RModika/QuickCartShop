package za.ac.cput.model;

public class UserAuth {
    private String email;
    private String password;

    public UserAuth() {}

    public UserAuth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
