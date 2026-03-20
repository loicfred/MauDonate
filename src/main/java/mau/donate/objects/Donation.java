package mau.donate.objects;

import jakarta.persistence.*;
import mau.donate.objects.enums.StorageStatus;
import my.loic.utilities.db.spring.DatabaseObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static my.loic.utilities.db.spring.DatabaseService.dbService;
import static my.loic.utilities.util.Utilities.StopString;

@Entity @Table
public class Donation extends DatabaseObject.ID_OBJ<Long, Donation> {
    @ManyToOne
    @JoinColumn(referencedColumnName = "ID", name = "DonorID")
    private transient User D = null;

    @ManyToOne
    @JoinColumn(referencedColumnName = "ID", name = "ReceiverID")
    private transient User R = null;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "ID", name = "DonationID")
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
        return D == null ? D = dbService.getById(User.class, DonorID).orElse(null) : D;
    }
    public User getReceiver() {
        return R == null ? R = dbService.getById(User.class, ReceiverID).orElse(null) : R;
    }

    public List<Donation_Item> getItems() {
        return items == null ? items = dbService.getAllWhere(Donation_Item.class, "DonationID = ?", ID) : items;
    }
    public void setItems(List<Donation_Item> items) {
        this.items = items;
    }

    public String getItemsAsString() {
        return getItems().stream().map(i -> "• " + i.Quantity + "x " + i.ItemName).collect(Collectors.joining("\n"));
    }

    public String getItemsStatus() {
        for (StorageStatus status : StorageStatus.values()) {
            if (getItems().stream().allMatch(i -> i.Status.equalsIgnoreCase(status.name()))) {
                return status.name();
            }
        }
        return StorageStatus.NOT_YET_BROUGHT.name();
    }
}