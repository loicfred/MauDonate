package mau.donate.objects;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import my.loic.utilities.db.spring.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static my.loic.utilities.db.spring.DatabaseService.dbService;

@Entity
@Table
public class Trip extends DatabaseObject.ID_OBJ<Long, Trip> {

    public Long StaffID;
    public Long DonationID;
    public String Address;
    public LocalDateTime ScheduleTime;
    public String StartLocation;
    public String EndDestination;

    public Trip() {}
    public Trip(long staffID, long donationID, String address, LocalDateTime scheduleTime, String startLocation, String endDestination) {
        ID = Instant.now().toEpochMilli();
        StaffID = staffID;
        DonationID = donationID;
        Address = address;
        ScheduleTime = scheduleTime;
        StartLocation = startLocation;
        EndDestination = endDestination;
        Write();
    }

    public static List<Trip> getByStaff(long staffId) {
        return dbService.getAllWhere(Trip.class, "StaffID = ?", staffId);
    }

    public static Trip getByDonation(long donationID) {
        return dbService.getWhere(Trip.class, "DonationID = ?", donationID).orElse(null);
    }

}