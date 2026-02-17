package mau.donate;

import mau.donate.objects.Donation_Request;
import mau.donate.objects.Vehicle;
import mau.donate.objects.derived.D_Donation_Request;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.List;

@EnableCaching
@SpringBootApplication
public class MauDonateApplication {


    static void main(String[] args) {
        SpringApplication.run(MauDonateApplication.class, args);
    }
}
