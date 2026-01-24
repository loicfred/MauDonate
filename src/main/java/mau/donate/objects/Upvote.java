package mau.donate.objects;

import mau.donate.service.database.DatabaseObject;

import java.time.Instant;

public class Upvote extends DatabaseObject.ID_OBJ<Long, Email_Verification> {
    private transient Donation D;
    private transient User U;

    public long UpvoterID;
    public long DonationID;

    public Upvote() {}
    public Upvote(User upvoter, Donation donation) {
        this.ID = Instant.now().toEpochMilli();
        this.UpvoterID = upvoter.getID();
        this.DonationID = donation.getID();
        Write();
    }

    public Donation getDonation() {
        return D == null ? D = Donation.getById(DonationID) : D;
    }
    public User getUpvoter() {
        return U == null ? U = User.getById(UpvoterID) : U;
    }

}
