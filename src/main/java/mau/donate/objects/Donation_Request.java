package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

public class Donation_Request extends DatabaseObject<Donation_Request> {
    public transient User U;

    public long ID;
    public long UserID;
    public String Story;
    public String AccountNumber;
    public String BankName;
    public double CurrentAmount;
    public double GoalAmount;
    public int Upvotes;
    public LocalDateTime CreatedAt;
    public LocalDateTime UpdatedAt;
    public boolean isApproved;
    public boolean isCompleted;

    public Donation_Request() {}
    public Donation_Request(long userId, String story, String accountNumber, String bankName, double goalAmount) {
        ID = Instant.now().toEpochMilli();
        UserID = userId;
        Story = story;
        AccountNumber = accountNumber;
        BankName = bankName;
        isApproved = false;
        isCompleted = false;
        CurrentAmount = 0;
        GoalAmount = goalAmount;
        Upvotes = 0;
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