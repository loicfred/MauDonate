package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.LocalDateTime;
import java.util.List;

import static my.utilities.util.Utilities.StopString;

public class Donation extends DatabaseObject<Donation> {
    public transient List<Donation_Item> Items = null;

    public long DonorID;
    public long ReceiverID;
    public double Rupees;
    public String Message;
    public boolean Approved;
    public boolean Anonymous;
    public LocalDateTime CreatedAt;
    public LocalDateTime UpdatedAt;

    public Donation() {}

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
    public boolean isApproved() {
        return Approved;
    }
    public boolean isAnonymous() {
        return Anonymous;
    }
    public LocalDateTime getCreatedAt() {
        return CreatedAt;
    }
    public LocalDateTime getUpdatedAt() {
        return UpdatedAt;
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
    public void setApproved(boolean approved) {
        Approved = approved;
    }
    public void setAnonymous(boolean anonymous) {
        Anonymous = anonymous;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        CreatedAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        UpdatedAt = updatedAt;
    }



    public static Donation getById(long id) {
        return DatabaseObject.getById(Donation.class, id).orElse(null);
    }

    public List<Donation_Item> getItems(long id) {
        return Items == null ? Items = DatabaseObject.getAllWhere(Donation_Item.class, "DonationID = ?", id) : Items;
    }
}