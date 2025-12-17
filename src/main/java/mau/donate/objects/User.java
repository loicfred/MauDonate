package mau.donate.objects;

import mau.donate.service.DatabaseObject;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static my.utilities.util.Utilities.CutString;

public class User extends DatabaseObject<User> {
    public transient Profile P;

    public long ID;
    public String Username;
    public String Email;
    public String Password;
    public String FirstName;
    public String LastName;
    public String Role;
    public String Address;
    public String Gender;
    public String Phone;
    public LocalDate DateOfBirth;
    public boolean isAnonymous = false;
    public boolean Enabled = false;
    public LocalDateTime CreatedAt = LocalDateTime.now();
    public LocalDateTime UpdatedAt = LocalDateTime.now();

    public User() {
        this.ID = Instant.now().toEpochMilli();
    }

    public User(String username, String email, String password, String firstName, String lastName, String role, String address, String gender, String phone, LocalDate dateOfBirth, boolean isAnonymous) {
        ID = Instant.now().toEpochMilli();
        Username = CutString(username, 64);
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

    public static User getByUsername(String username) {
        return DatabaseObject.getWhere(User.class, "Username = ?", username).orElse(null);
    }

    public static User getByEmail(String email) {
        return DatabaseObject.getWhere(User.class, "Email = ?", email).orElse(null);
    }

    public static User getByLogin(String username, String password) {
        return DatabaseObject.getWhere(User.class, "(Username = ? OR Email = ?) AND Password = ?", username, username, password).orElse(null);
    }

    public static void ClearFailedLogins(String username, String email) {
        User U = DatabaseObject.getWhere(User.class, "Username = ? OR Enabled = ?", username, false).orElse(null);
        if (U != null) U.Delete();
        U = DatabaseObject.getWhere(User.class, "Email = ? OR Enabled = ?", email, false).orElse(null);
        if (U != null) U.Delete();
    }

    public static int ClearAllFailedLogins() {
        return DatabaseObject.doUpdate("DELETE user WHERE Enabled = ?", false);
    }

    public long getId() {
        return ID;
    }
    public String getUsername() {
        return Username;
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
    public boolean isEnabled() {
        return Enabled;
    }

    public void setUsername(String username) {
        Username = CutString(username, 64);
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

}