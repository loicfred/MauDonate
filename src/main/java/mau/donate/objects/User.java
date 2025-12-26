package mau.donate.objects;

import mau.donate.service.DatabaseObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static my.utilities.util.Utilities.CutString;

public class User extends DatabaseObject<User> {

    public long ID;
    public String Email;
    public String Password;
    public String FirstName;
    public String LastName;
    public String AccountProvider = null;
    public String Role = "USER";
    public String Address;
    public String Gender;
    public String Phone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDate DateOfBirth;
    public boolean isAnonymous = false;
    public boolean Enabled = false;
    public boolean Verified = false;
    public LocalDateTime CreatedAt = LocalDateTime.now();
    public LocalDateTime UpdatedAt = LocalDateTime.now();

    public User() {
        this.ID = Instant.now().toEpochMilli();
    }

    public User(String email, String password, String firstName, String lastName, String role, String address, String gender, String phone, LocalDate dateOfBirth, boolean isAnonymous) {
        ID = Instant.now().toEpochMilli();
        Email = CutString(email, 64);
        Password = password;
        FirstName = CutString(firstName, 64);
        LastName = CutString(lastName, 64);
        Role = CutString(role, 16);
        Address = CutString(address, 128);
        Gender = gender;
        Phone = phone;
        DateOfBirth = dateOfBirth;
        Write();
    }

    public static User getById(long id) {
        return DatabaseObject.getById(User.class, id).orElse(null);
    }

    public static User getByEmail(String email) {
        return DatabaseObject.getWhere(User.class, "Email = ?", email).orElse(null);
    }

    public static User getByAuthentication(Principal principal) {
        if (principal == null) return null;
        return DatabaseObject.getWhere(User.class, "Email = ?", principal.getName()).orElse(null);
    }

    public static void ClearFailedLogins(String email) {
        User U = DatabaseObject.getWhere(User.class, "Email = ? OR (Enabled = ? AND Verified = ?)", email, false, false).orElse(null);
        if (U != null) U.Delete();
    }

    public long getID() {
        return ID;
    }
    public String getEmail() {
        return Email;
    }
    public String getPassword() {
        return Password;
    }
    public LocalDateTime getCreatedAt() {
        return CreatedAt;
    }
    public boolean isAnonymous() {
        return isAnonymous;
    }
    public LocalDate getDateOfBirth() {
        return DateOfBirth;
    }
    public String getPhone() {
        return Phone;
    }
    public String getGender() {
        return Gender;
    }
    public String getAddress() {
        return Address;
    }
    public String getRole() {
        return Role;
    }
    public String getLastName() {
        return LastName;
    }
    public String getFirstName() {
        return FirstName;
    }
    public String getAccountProvider() {
        return AccountProvider;
    }
    public boolean isEnabled() {
        return Enabled;
    }
    public boolean isVerified() {
        return Verified;
    }

    public void setID(long id) {
        ID = id;
    }
    public void setPassword(String password) {
        Password = password;
    }
    public void setEmail(String email) {
        Email = CutString(email, 64);
    }
    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }
    public void setVerified(boolean verified) {
        Verified = verified;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        CreatedAt = createdAt;
    }
    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }
    public void setPhone(String phone) {
        Phone = phone;
    }
    public void setGender(String gender) {
        Gender = gender;
    }
    public void setAddress(String address) {
        Address = CutString(address, 128);
    }
    public void setRole(String role) {
        Role = CutString(role, 16);
    }
    public void setLastName(String lastName) {
        LastName = CutString(lastName, 64);
    }
    public void setFirstName(String firstName) {
        FirstName = CutString(firstName, 64);
    }
    public void setAccountProvider(String accountProvider) {
        AccountProvider = accountProvider;
    }


    public boolean isPasswordValid() {
        if (Password.length() < 8) return false;
        boolean hasSymbol = false;
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNumber = false;

        for (char c : Password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasNumber = true;
            else if (!Character.isLetterOrDigit(c)) hasSymbol = true;
        }
        return hasSymbol && hasUpper && hasLower && hasNumber;
    }
}