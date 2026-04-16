package mau.donate.objects;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.solarframework.db.spring.DatabaseObject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.solarframework.db.spring.DatabaseService.dbService;

public class Email_Verification extends DatabaseObject.ID_OBJ<Long, Email_Verification> {
    @ManyToOne
    @JoinColumn(referencedColumnName = "ID", name = "UserID")
    private transient User U;

    public long UserID;
    public String Token;
    public String Type;
    public Long ExpiryDate;

    public Email_Verification() {}
    public Email_Verification(User user, String token, String type) {
        this.ID = Instant.now().toEpochMilli();
        this.UserID = user.ID;
        this.Token = token;
        this.Type = type;
        this.ExpiryDate = Instant.now().plus(24, ChronoUnit.HOURS).toEpochMilli();
        Write();
    }

    public static Email_Verification getByToken(String token) {
        return dbService.getWhere(Email_Verification.class, "Token = ?", token).orElse(null);
    }

    public User getUser() {
        return U == null ? U = dbService.getById(User.class, UserID).orElse(null) : U;
    }

    public static void ClearUnregisterUsers() {
        for (Email_Verification vToken : dbService.getAllWhere(Email_Verification.class, "Type = ? AND ExpiryDate < ?","REGISTRATION", Instant.now().toEpochMilli())) {
            vToken.getUser().Delete();
        }
    }
}
