package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static my.utilities.util.Utilities.CutString;
import static my.utilities.util.Utilities.StopString;

public class Donation extends DatabaseObject<Donation> {
    public transient List<Donation_Item> Items = null;

    public long ID;
    public long DonorID;
    public long ReceiverID;
    public double Rupees;
    public String Message;
    public String Status;
    public boolean isAnonymous;
    public LocalDateTime CreatedAt;

    public Donation() {}
    public Donation(long donorID, long receiverID, double rupees, String message, String status, boolean isAnonymous) {
        this.ID = Instant.now().toEpochMilli();
        DonorID = donorID;
        ReceiverID = receiverID;
        Rupees = rupees;
        Message = CutString(message, 512);
        Status = status;
        this.isAnonymous = isAnonymous;
        CreatedAt = LocalDateTime.now();
        Write();
    }

    public long getID() {
        return ID;
    }
    public long getDonorID() {
        return DonorID;
    }
    public long getReceiverID() {
        return ReceiverID;
    }
    public double getRupees() {
        return Rupees;
    }
    public String getMessage() {
        return Message;
    }
    public String getStatus() {
        return Status;
    }
    public boolean isAnonymous() {
        return isAnonymous;
    }
    public LocalDateTime getCreatedAt() {
        return CreatedAt;
    }


    public void setID(long ID) {
        this.ID = ID;
    }
    public void setDonorID(long donorID) {
        DonorID = donorID;
    }
    public void setReceiverID(long receiverID) {
        ReceiverID = receiverID;
    }
    public void setRupees(double rupees) {
        Rupees = rupees;
    }
    public void setMessage(String message) {
        Message = StopString(message, 512);
    }
    public void setStatus(String status) {
        Status = status;
    }
    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        CreatedAt = createdAt;
    }



    public static Donation getById(long id) {
        return DatabaseObject.getById(Donation.class, id).orElse(null);
    }

    public List<Donation_Item> getItems(long id) {
        return Items == null ? Items = DatabaseObject.getAllWhere(Donation_Item.class, "DonationID = ?", id) : Items;
    }
}