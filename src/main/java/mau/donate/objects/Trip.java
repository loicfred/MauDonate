package mau.donate.objects;

import mau.donate.service.database.DatabaseObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class Trip extends DatabaseObject<Trip> {

    public long StaffID;
    public long DonationID;
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

    public static Trip getById(long id) {
        return DatabaseObject.getById(Trip.class, id).orElse(null);
    }

    public static List<Trip> getByStaff(long staffId) {
        return DatabaseObject.getAllWhere(Trip.class, "StaffID = ?", staffId);
    }

    public static Trip getByDonation(long donationID) {
        return DatabaseObject.getWhere(Trip.class, "DonationID = ?", donationID).orElse(null);
    }

}