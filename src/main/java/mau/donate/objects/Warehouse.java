package mau.donate.objects;

import mau.donate.objects.enums.StorageStatus;
import mau.donate.service.database.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class Warehouse extends DatabaseObject.ID_OBJ<Long, Warehouse> {
    public transient List<Donation_Item> items = null;

    public String Name;
    public String Region;
    public String Address;
    public long MaxCapacity;
    public LocalDateTime CreatedAt;

    public Warehouse() {}

    public Warehouse(String name, String region, String address, long maxCapacity) {
        ID = Instant.now().toEpochMilli();
        Name = name;
        Region = region;
        Address = address;
        MaxCapacity = maxCapacity;
        CreatedAt = LocalDateTime.now();
        Write();
    }

    public List<Donation_Item> getItems() {
        return items == null ? items = DatabaseObject.getAllWhere(Donation_Item.class, "WarehouseID = ? AND NOT Status = ? ORDER BY Quantity * CapacityPerQty DESC", ID, StorageStatus.DELIVERED.name()) : items;
    }
    private void setItems(List<Donation_Item> items) {
        this.items = items;
    }

    public static Warehouse getById(long id) {
        return DatabaseObject.getById(Warehouse.class, id).orElse(null);
    }
}