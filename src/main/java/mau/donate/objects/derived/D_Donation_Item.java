package mau.donate.objects.derived;

import mau.donate.objects.Donation_Item;

import java.time.LocalDateTime;

public class D_Donation_Item extends Donation_Item {

    public LocalDateTime CreatedAt;
    public String Message;
    public String Sender_FName;
    public String Sender_LName;
    public String Sender_Address;
    public String Receiver_FName;
    public String Receiver_LName;
    public String Receiver_Address;

}
