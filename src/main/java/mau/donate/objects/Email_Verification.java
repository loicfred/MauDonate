package mau.donate.objects;

import mau.donate.service.database.DatabaseObject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Email_Verification extends DatabaseObject.ID_OBJ<Long, Email_Verification> {
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

    public static Email_Verification getById(long id) {
        return DatabaseObject.getById(Email_Verification.class, id).orElse(null);
    }
    public static Email_Verification getByToken(String token) {
        return DatabaseObject.getWhere(Email_Verification.class, "Token = ?", token).orElse(null);
    }

    public User getUser() {
        return U == null ? U = User.getById(UserID) : U;
    }

    public static void ClearUnregisterUsers() {
        for (Email_Verification vToken : DatabaseObject.getAllWhere(Email_Verification.class, "Type = ? AND ExpiryDate < ?","REGISTRATION", Instant.now().toEpochMilli())) {
            vToken.getUser().Delete();
        }
    }
}
