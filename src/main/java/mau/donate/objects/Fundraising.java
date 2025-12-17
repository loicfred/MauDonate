package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

import static my.utilities.util.Utilities.StopString;

public class Fundraising extends DatabaseObject<Fundraising> {
    public long ID;
    public long DonorID;
    public double USD;
    public String Title;
    public String Comment;
    public LocalDateTime CreatedAt;
    
    public Fundraising() {}
    public Fundraising(long donorID, double usd, String title, String comment) {
        ID = Instant.now().toEpochMilli();
        DonorID = donorID;
        USD = usd;
        Title = StopString(title, 128);
        Comment = StopString(comment, 512);
        CreatedAt = LocalDateTime.now();
        Write();
    }

    public static Fundraising getById(long id) {
        return DatabaseObject.getById(Fundraising.class, id).orElse(null);
    }
}