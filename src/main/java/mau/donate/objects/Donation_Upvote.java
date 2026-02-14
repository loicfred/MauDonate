package mau.donate.objects;

import mau.donate.service.database.DatabaseObject;

import java.util.List;

public class Donation_Upvote extends DatabaseObject<Donation_Upvote> {

    public long UserID;
    public long RequestID;

    public Donation_Upvote() {}
    public Donation_Upvote(long userID, long requestID) {
        this.UserID = userID;
        this.RequestID = requestID;
        Write();
    }

    public static Donation_Upvote getById(long userId, long requestID) {
        return DatabaseObject.getWhere(Donation_Upvote.class, "UserID = ? AND RequestID = ?", userId, requestID).orElse(null);
    }

    public long getUserID() {
        return UserID;
    }
    public void setUserID(long userID) {
        UserID = userID;
    }

    public long getRequestID() {
        return RequestID;
    }
    public void setRequestID(long requestID) {
        RequestID = requestID;
    }

    @Override
    protected List<String> IDFields() {
        return List.of("UserID", "RequestID");
    }
}
