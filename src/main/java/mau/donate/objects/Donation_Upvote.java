package mau.donate.objects;

import jakarta.persistence.*;
import my.loic.utilities.db.spring.DatabaseObject;

import static my.loic.utilities.db.spring.DatabaseService.dbService;

@Entity
@Table
@IdClass(Donation_Upvote.class)
public class Donation_Upvote extends DatabaseObject<Donation_Upvote> {
    @Id
    public long UserID;
    @Id
    public long RequestID;

    public Donation_Upvote() {}
    public Donation_Upvote(long userID, long requestID) {
        this.UserID = userID;
        this.RequestID = requestID;
        Write();
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

}
