package mau.donate.objects;

import jakarta.persistence.*;
import mau.donate.objects.enums.StorageStatus;
import my.loic.utilities.db.spring.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static my.loic.utilities.db.spring.DatabaseService.dbService;

@Entity
@Table
public class Warehouse extends DatabaseObject.ID_OBJ<Long, Warehouse> {
    @OneToMany
    @JoinColumn(referencedColumnName = "ID", name = "WarehouseID")
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

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getRegion() {
        return Region;
    }
    public void setRegion(String region) {
        Region = region;
    }

    public String getAddress() {
        return Address;
    }
    public void setAddress(String address) {
        Address = address;
    }

    public long getMaxCapacity() {
        return MaxCapacity;
    }
    public void setMaxCapacity(long maxCapacity) {
        MaxCapacity = maxCapacity;
    }

    public LocalDateTime getCreatedAt() {
        return CreatedAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        CreatedAt = createdAt;
    }

    public List<Donation_Item> getItems() {
        return items == null ? items = dbService.getAllWhere(Donation_Item.class, "WarehouseID = ? AND NOT Status = ? ORDER BY Quantity * CapacityPerQty DESC", ID, StorageStatus.DELIVERED.name()) : items;
    }
    private void setItems(List<Donation_Item> items) {
        this.items = items;
    }

}