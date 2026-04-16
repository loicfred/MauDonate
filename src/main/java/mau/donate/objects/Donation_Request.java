package mau.donate.objects;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.solarframework.db.spring.DatabaseObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import static org.solarframework.db.spring.DatabaseService.dbService;

@Entity
@Table
public class Donation_Request extends DatabaseObject.ID_OBJ<Long, Donation_Request> {
    @ManyToOne
    @JoinColumn(referencedColumnName = "ID", name = "UserID")
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
        return U == null ? U = dbService.getById(User.class, UserID).orElse(null) : U;
    }

    public String getGoalAmountString() {
        return new DecimalFormat("#,###.##").format(GoalAmount);
    }

    public boolean hasUserUpvoted(long userID) {
        return dbService.getWhere(Donation_Upvote.class, "UserID = ? AND RequestID = ?", userID, ID).orElse(null) != null;
    }
}