package mau.donate.objects;

import jakarta.persistence.*;
import mau.donate.objects.enums.DonationStatus;
import org.solarframework.web.auth.obj.Account_User;
import org.solarframework.db.spring.DatabaseObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table
public class Donation_Request extends DatabaseObject.ID_OBJ_RECORD<Long, Donation_Request> {
    @ManyToOne
    @JoinColumn(referencedColumnName = "ID", name = "UserID")
    public transient Account_User U;

    public long UserID;
    public String Title;
    public String Message;
    public String AccountNumber;
    public String BankName;
    public double CurrentAmount;
    public double GoalAmount;
    @Enumerated(EnumType.STRING)
    public DonationStatus Status;
    public boolean Approved;
    public boolean Completed;

    public Donation_Request() {}


    public String getTitle() {
        return Title;
    }
    public String getMessage() {
        return Message;
    }
    public String getAccountNumber() {
        return AccountNumber;
    }
    public String getBankName() {
        return BankName;
    }
    public double getCurrentAmount() {
        return CurrentAmount;
    }
    public double getGoalAmount() {
        return GoalAmount;
    }
    public DonationStatus getStatus() {
        return Status;
    }
    public boolean isApproved() {
        return Approved;
    }
    public boolean isCompleted() {
        return Completed;
    }

    public void setTitle(String title) {
        Title = title;
    }
    public void setMessage(String message) {
        Message = message;
    }
    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }
    public void setBankName(String bankName) {
        BankName = bankName;
    }
    public void setCurrentAmount(double currentAmount) {
        CurrentAmount = currentAmount;
    }
    public void setGoalAmount(double goalAmount) {
        GoalAmount = goalAmount;
    }
    public void setStatus(DonationStatus status) {
        Status = status;
    }
    public void setApproved(boolean approved) {
        Approved = approved;
    }
    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public Account_User getUser() {
        return U == null ? U = SolarDBManager.getById(Account_User.class, UserID).orElse(null) : U;
    }

    public String getGoalAmountString() {
        return new DecimalFormat("#,###.##").format(GoalAmount);
    }

    public boolean hasUserUpvoted(long userID) {
        return SolarDBManager.getWhere(Donation_Upvote.class, "UserID = ? AND RequestID = ?", userID, ID).orElse(null) != null;
    }
}