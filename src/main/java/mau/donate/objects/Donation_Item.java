package mau.donate.objects;

import mau.donate.objects.enums.StorageStatus;
import mau.donate.service.database.DatabaseObject;

import java.text.DecimalFormat;

import static my.utilities.util.Utilities.StopString;

public class Donation_Item extends DatabaseObject.ID_OBJ<Long, Donation_Item> {
    public long DonationID;
    public Long WarehouseID;
    public String ItemName;
    public int Quantity;
    public long CapacityPerQty = 0;
    public String Status = StorageStatus.NOT_YET_BROUGHT.toString(); // NOT_YET_BROUGHT, IN_STORAGE, DELIVERED;

    public Donation_Item() {}

    public String getPercentage(double MaxCapacity) {
        return new DecimalFormat("#.##").format((CapacityPerQty * Quantity) / MaxCapacity * 100.0);
    }

    public String getItemName() {
        return ItemName;
    }
    public void setItemName(String itemName) {
        ItemName = StopString(itemName, 128);
    }

    public int getQuantity() {
        return Quantity;
    }
    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getStatus() {
        return Status;
    }
    public void setStatus(String status) {
        Status = status;
    }


}