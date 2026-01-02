package mau.donate.objects;

import mau.donate.service.database.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

public class Vehicle extends DatabaseObject<Vehicle> {

    public String Name;
    public int TripsCarriedOut;
    public long CarriageCapacity;
    public LocalDateTime CreatedAt;

    public Vehicle() {}
    public Vehicle(String name, int tripsCarriedOut, long carriageCapacity) {
        ID = Instant.now().toEpochMilli();
        Name = name;
        TripsCarriedOut = tripsCarriedOut;
        CarriageCapacity = carriageCapacity;
        CreatedAt = LocalDateTime.now();
        Write();
    }

    public static Vehicle getById(long id) {
        return DatabaseObject.getById(Vehicle.class, id).orElse(null);
    }
}