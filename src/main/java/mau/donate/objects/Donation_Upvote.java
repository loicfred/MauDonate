package mau.donate.objects;

import jakarta.persistence.*;
import org.solarframework.db.spring.DatabaseObject;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table
@IdClass(Donation_Upvote.UserRequest.class)
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


    @Embeddable
    protected class UserRequest implements Serializable {
        private Long UserID;
        private Long RequestID;

        public Long getUserID() {
            return UserID;
        }
        public void setUserID(Long userID) {
            UserID = userID;
        }

        public Long getRequestID() {
            return RequestID;
        }
        public void setRequestID(Long requestID) {
            RequestID = requestID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserRequest that)) return false;
            return Objects.equals(this.UserID, that.UserID) && Objects.equals(this.RequestID, that.RequestID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(UserID, RequestID);
        }
    }
}