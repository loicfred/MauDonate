package mau.donate.objects;

import mau.donate.service.database.DatabaseObject;

import java.time.LocalDateTime;
import java.util.List;

import static my.utilities.util.Utilities.StopString;

public class Donation extends DatabaseObject.ID_OBJ<Long, Donation> {
    private transient User D = null;
    private transient User R = null;
    public transient List<Donation_Item> items = null;

    public long DonorID;
    public long ReceiverID;
    public double Rupees;
    public String Message;
    public boolean Approved;
    public boolean Anonymous;
    public LocalDateTime CreatedAt;
    public LocalDateTime UpdatedAt;

    public Donation() {}

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

    public User getDonor() {
        return D == null ? D = User.getById(DonorID) : D;
    }
    public User getReceiver() {
        return R == null ? R = User.getById(ReceiverID) : R;
    }

    public static Donation getById(long id) {
        return DatabaseObject.getById(Donation.class, id).orElse(null);
    }

    public List<Donation_Item> getItems() {
        return items == null ? items = DatabaseObject.getAllWhere(Donation_Item.class, "DonationID = ?", ID) : items;
    }
    public void setItems(List<Donation_Item> items) {
        this.items = items;
    }
}