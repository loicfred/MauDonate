package mau.donate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan({"mau.donate", "org.solarframework.*"})
public class MauDonateApplication {

    static void main(String[] args) {
        SpringApplication.run(MauDonateApplication.class, args);
    }

}
