package mau.donate.objects;

import mau.donate.service.database.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

public class Association extends DatabaseObject.ID_OBJ<Long, Association> {

    public String Name;
    public String Description;
    public byte[] Image;
    public LocalDateTime JoinedAt;
    public LocalDateTime CanceledAt;

    public Association() {}
    public Association(String name, String description, byte[] image, LocalDateTime joinedAt, LocalDateTime canceledAt) {
        this.ID = Instant.now().toEpochMilli();
        Name = name;
        Description = description;
        Image = image;
        JoinedAt = joinedAt;
        CanceledAt = LocalDateTime.now();
        Write();
    }

    public static Association getById(long id) {
        return DatabaseObject.getById(Association.class, id).orElse(null);
    }
}
