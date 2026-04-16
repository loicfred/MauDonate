package mau.donate.objects;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.solarframework.db.spring.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.solarframework.db.spring.DatabaseService.dbService;
import static org.solarframework.core.util.StringUtils.StopString;

@Entity
@Table
public class Campaign extends DatabaseObject.ID_OBJ<Long, Campaign> {
    @ManyToOne
    @JoinColumn(referencedColumnName = "ID", name = "AssociationID")
    private transient Association A = null;

    public long AssociationID;
    public String Title;
    public String Message;
    public String Hyperlink;
    public byte[] Image;
    public LocalDateTime CreatedAt;
    public LocalDateTime StartAt;
    public LocalDateTime UpdatedAt;

    public Campaign() {}
    public Campaign(long associationID, String title, String message, String hyperlink, LocalDateTime startAt, byte[] image) {
        this.ID = Instant.now().toEpochMilli();
        AssociationID = associationID;
        Title = StopString(title, 128);
        Message = StopString(message, 512);
        Hyperlink = hyperlink;
        Image = image;
        StartAt = startAt;
        CreatedAt = LocalDateTime.now();
        UpdatedAt = LocalDateTime.now();
        Write();
    }

    public byte[] getImage() {
        return Image == null ? Image = refetchAttribute("Image", byte[].class) : Image;
    }
    public void setImage(byte[] image) {
        Image = image;
        UpdateOnly("Image");
    }

    public Association getAssociation() {
        return A == null ? A = dbService.getById(Association.class, AssociationID).orElse(null) : A;
    }

}