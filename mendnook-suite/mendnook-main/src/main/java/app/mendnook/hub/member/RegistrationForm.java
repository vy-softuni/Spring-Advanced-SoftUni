package app.mendnook.hub.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Enter your name")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String displayName;

    @NotBlank(message = "Enter an email address")
    @Email(message = "Enter a valid email address")
    @Size(max = 160)
    private String email;

    @NotBlank(message = "Enter a password")
    @Size(min = 10, max = 72, message = "Password must contain between 10 and 72 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain a letter and a number")
    private String password;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
