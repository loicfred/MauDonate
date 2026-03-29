package mau.donate.objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import my.loic.utilities.db.spring.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table
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

}
