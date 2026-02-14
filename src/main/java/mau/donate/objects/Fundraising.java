package mau.donate.objects;

import mau.donate.objects.enums.PaymentStatus;
import mau.donate.service.database.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

import static my.utilities.util.Utilities.StopString;

public class Fundraising extends DatabaseObject.ID_OBJ<String, Fundraising> {
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

    public static Fundraising getById(String id) {
        return DatabaseObject.getById(Fundraising.class, id).orElse(null);
    }

}