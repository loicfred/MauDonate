package mau.donate.objects;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mau.donate.objects.enums.PaymentStatus;
import org.solarframework.db.spring.DatabaseObject;

import java.time.LocalDateTime;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;
import static org.solarframework.core.util.StringUtils.StopString;

@Entity
@Table
public class Fundraising extends DatabaseObject.ID_OBJ<String, Fundraising> {
    @ManyToOne
    @JoinColumn(referencedColumnName = "ID", name = "DonorID")
    private transient User D = null;

    public long DonorID;
    public double USD;
    public String Title;
    public String Message;
    public String Status = PaymentStatus.PENDING.toString();
    public LocalDateTime CreatedAt;
    
    public Fundraising() {}
    public Fundraising(String id, long donorID, double usd, String title, String message) {
        ID = id;
        DonorID = donorID;
        USD = usd;
        Title = StopString(title, 128);
        Message = StopString(message, 512);
        CreatedAt = LocalDateTime.now();
        Write();
    }

    public User getDonor() {
        return D == null ? D = SolarDBManager.getById(User.class, DonorID).orElse(null) : D;
    }

}