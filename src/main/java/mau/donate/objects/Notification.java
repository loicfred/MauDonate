package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static my.utilities.util.Utilities.StopString;

public class Notification extends DatabaseObject<Notification> {

    public long UserID;
    public String Title;
    public String Message;
    public LocalDateTime CreatedAt;
    public boolean isRead = false;

    public Notification() {}
    public Notification(long userID, String title, String message) {
        ID = Instant.now().toEpochMilli();
        UserID = userID;
        Title = StopString(title, 128);
        Message = StopString(message, 512);
        CreatedAt = LocalDateTime.now();
        Write();
    }

    public long getID() {
        return ID;
    }
    public long getUserID() {
        return UserID;
    }
    public String getTitle() {
        return Title;
    }
    public String getMessage() {
        return Message;
    }
    public LocalDateTime getCreatedAt() {
        return CreatedAt;
    }
    public boolean isRead() {
        return isRead;
    }

    public void setID(long id) {
        ID = id;
    }
    public void setUserID(long userID) {
        UserID = userID;
    }
    public void setTitle(String title) {
        Title = StopString(title, 128);
    }
    public void setMessage(String message) {
        Message = StopString(message, 512);
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        CreatedAt = createdAt;
    }
    public void setRead(boolean read) {
        isRead = read;
    }


    public static Notification getById(long id) {
        return DatabaseObject.getById(Notification.class, id).orElse(null);
    }

    public static List<Notification> ofUser(long userID, int limit) {
        return DatabaseObject.getAllWhere(Notification.class, "UserID = ? ORDER BY ID DESC LIMIT ?;", userID, limit);
    }
}
