package mau.donate.objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import my.loic.utilities.db.spring.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;

import static my.loic.utilities.db.spring.DatabaseService.dbService;

@Entity @Table
public class Vehicle extends DatabaseObject.ID_OBJ<Long, Vehicle> {

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

}