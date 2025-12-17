package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.Instant;

public class Donation_Item extends DatabaseObject<Donation_Item> {
    public long ID;
    public long DonationID;
    public long WarehouseID;
    public String ItemName;
    public int Quantity;
    public long CapacityPerQty;

    public Donation_Item() {}
    public Donation_Item(long donationID, long warehouseID, String itemName, int quantity, long capacityPerQty) {
        ID = Instant.now().toEpochMilli();
        DonationID = donationID;
        WarehouseID = warehouseID;
        ItemName = itemName;
        Quantity = quantity;
        CapacityPerQty = capacityPerQty;
        Write();
    }

}