package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class Notification extends DatabaseObject<Notification> {

    public long ID;
    public long UserID;
    public String Title;
    public String Content;
    public LocalDateTime CreatedAt;
    public boolean isRead = false;

    public Notification() {}
    public Notification(long userID, String title, String content) {
        ID = Instant.now().toEpochMilli();
        UserID = userID;
        Title = title;
        Content = content;
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
    public String getContent() {
        return Content;
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
        Title = title;
    }
    public void setContent(String content) {
        Content = content;
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
