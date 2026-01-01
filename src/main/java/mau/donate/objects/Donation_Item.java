package mau.donate.objects;

import mau.donate.objects.enums.StorageStatus;
import mau.donate.service.DatabaseObject;

import java.text.DecimalFormat;
import java.time.Instant;

public class Donation_Item extends DatabaseObject<Donation_Item> {
    public long DonationID;
    public long WarehouseID;
    public String ItemName;
    public int Quantity;
    public long CapacityPerQty;
    public String Status; // NOT_YET_BROUGHT, IN_STORAGE, DELIVERED;

    public Donation_Item() {}
    public Donation_Item(long donationID, long warehouseID, String itemName, int quantity, long capacityPerQty) {
        ID = Instant.now().toEpochMilli();
        DonationID = donationID;
        WarehouseID = warehouseID;
        ItemName = itemName;
        Quantity = quantity;
        CapacityPerQty = capacityPerQty;
        Status = StorageStatus.NOT_YET_BROUGHT.name();
        Write();
    }

    public String getPercentage(double MaxCapacity) {
        return new DecimalFormat("#.##").format((CapacityPerQty * Quantity) / MaxCapacity * 100.0);
    }


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}