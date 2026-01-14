package mau.donate.objects.derived;

import mau.donate.objects.Donation;
import mau.donate.objects.enums.StorageStatus;

import java.util.List;

public class D_Donation extends Donation {

    public String Sender_FName;
    public String Sender_LName;
    public String Sender_Address;
    public String Receiver_FName;
    public String Receiver_LName;
    public String Receiver_Address;

    public static List<D_Donation> getNotBroughtDonations() {
        return doQueryAll(D_Donation.class, """
                SELECT DISTINCT D.* FROM maudonate.d_donation D
                JOIN maudonate.donation_item I ON I.DonationID = D.ID
                WHERE I.Status = ?;""", StorageStatus.NOT_YET_BROUGHT.name());
    }

    public static List<D_Donation> getStoredDonations() {
        return doQueryAll(D_Donation.class, """
                SELECT DISTINCT D.* FROM maudonate.d_donation D
                JOIN maudonate.donation_item I ON I.DonationID = D.ID
                WHERE I.Status = ?;""", StorageStatus.IN_STORAGE.name());
    }
}
