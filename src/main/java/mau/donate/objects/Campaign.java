package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

public class Campaign extends DatabaseObject<Campaign> {
    private transient Association A;

    public long ID;
    public long AssociationID;
    public String Name;
    public String Description;
    public String Hyperlink;
    public byte[] Image;
    public LocalDateTime CreatedAt;
    public LocalDateTime StartAt;
    public LocalDateTime UpdatedAt;

    public Campaign() {}
    public Campaign(long associationID, String name, String description, String hyperlink, LocalDateTime startAt, byte[] image) {
        this.ID = Instant.now().toEpochMilli();
        AssociationID = associationID;
        Name = name;
        Description = description;
        Hyperlink = hyperlink;
        Image = image;
        StartAt = startAt;
        CreatedAt = LocalDateTime.now();
        UpdatedAt = LocalDateTime.now();
        Write();
    }

    public static Campaign getById(long id) {
        return DatabaseObject.getById(Campaign.class, id).orElse(null);
    }
}