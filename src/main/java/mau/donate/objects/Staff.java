package mau.donate.objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import my.loic.utilities.db.spring.DatabaseObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table
public class Staff extends DatabaseObject.ID_OBJ<Long, User> {

    public String FirstName;
    public String LastName;
    public String Department;
    public double Salary;
    public String Status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDate DateOfBirth;
    public String Gender;
    public String Address;
    public String Email;
    public String Phone;
    public LocalDateTime HiredAt;
    public LocalDateTime UpdatedAt;

    public Staff() {
        this.ID = Instant.now().toEpochMilli();
    }
    public Staff(String phone, String email, String address, String gender, LocalDate dateOfBirth, String status, double salary, String department, String lastName, String firstName) {
        ID = Instant.now().toEpochMilli();
        Phone = phone;
        Email = email;
        Address = address;
        Gender = gender;
        DateOfBirth = dateOfBirth;
        Status = status;
        Salary = salary;
        Department = department;
        LastName = lastName;
        FirstName = firstName;
        HiredAt = LocalDateTime.now();
        UpdatedAt = LocalDateTime.now();
        Write();
    }
}