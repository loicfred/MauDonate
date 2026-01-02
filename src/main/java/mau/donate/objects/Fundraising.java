package mau.donate.objects;

import mau.donate.service.database.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

import static my.utilities.util.Utilities.StopString;

public class Fundraising extends DatabaseObject<Fundraising> {
    public long DonorID;
    public double USD;
    public String Title;
    public String Message;
    public LocalDateTime CreatedAt;
    
    public Fundraising() {}
    public Fundraising(long donorID, double usd, String title, String message) {
        ID = Instant.now().toEpochMilli();
        DonorID = donorID;
        USD = usd;
        Title = StopString(title, 128);
        Message = StopString(message, 512);
        CreatedAt = LocalDateTime.now();
        Write();
    }

    public static Fundraising getById(long id) {
        return DatabaseObject.getById(Fundraising.class, id).orElse(null);
    }
}