package mau.donate.objects;

import mau.donate.objects.enums.Status;
import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

import static my.utilities.util.Utilities.StopString;

public class Donation_Request extends DatabaseObject<Donation_Request> {
    public transient User U;

    public long ID;
    public long UserID;
    public String Title;
    public String Message;
    public String AccountNumber;
    public String BankName;
    public double CurrentAmount;
    public double GoalAmount;
    public String Status;
    public LocalDateTime CreatedAt;
    public LocalDateTime UpdatedAt;
    public boolean Approved;
    public boolean Completed;

    public Donation_Request() {}
    public Donation_Request(long userId, String title, String message, String accountNumber, String bankName, double goalAmount) {
        ID = Instant.now().toEpochMilli();
        UserID = userId;
        Title = StopString(title, 128);
        Message = StopString(message, 512);
        AccountNumber = accountNumber;
        BankName = bankName;
        Approved = false;
        Completed = false;
        CurrentAmount = 0;
        GoalAmount = goalAmount;
        Status = mau.donate.objects.enums.Status.PENDING.toString();
        CreatedAt = LocalDateTime.now();
        UpdatedAt = LocalDateTime.now();
        Write();
    }

    public User getUser() {
        return U == null ? U = User.getById(UserID) : U;
    }

    public static Donation_Request getById(long id) {
        return DatabaseObject.getById(Donation_Request.class, id).orElse(null);
    }
}