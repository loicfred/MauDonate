package mau.donate.objects;

import mau.donate.service.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

public class Warehouse extends DatabaseObject<Warehouse> {

    public long ID;
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

    public static Warehouse getById(long id) {
        return DatabaseObject.getById(Warehouse.class, id).orElse(null);
    }
}