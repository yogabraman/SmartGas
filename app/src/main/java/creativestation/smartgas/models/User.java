package creativestation.smartgas.models;

public class User {

    private String username;
    private String email;
    private String alat;

    public User(String username, String email, String alat) {
        this.username = username;
        this.email = email;
        this.alat = alat;
    }

    public String getAlat() {
        return alat;
    }

    public void setAlat(String alat) {
        this.alat = alat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

