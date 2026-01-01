package mau.donate.objects;

import mau.donate.objects.enums.DonationStatus;
import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

import static my.utilities.util.Utilities.StopString;

public class Donation_Request extends DatabaseObject<Donation_Request> {
    public transient User U;

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

    public double getGoalAmount() {
        return GoalAmount;
    }

    public void setGoalAmount(double goalAmount) {
        GoalAmount = goalAmount;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public User getUser() {
        return U == null ? U = User.getById(UserID) : U;
    }

    public static Donation_Request getById(long id) {
        return DatabaseObject.getById(Donation_Request.class, id).orElse(null);
    }
}