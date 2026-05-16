package mau.donate;

import mau.donate.objects.Donation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;


@SpringBootApplication
@ComponentScan({"mau.donate", "org.solarframework.*"})
public class MauDonateApplication {

    static void main(String[] args) {
        SpringApplication.run(MauDonateApplication.class, args);
    }

}
