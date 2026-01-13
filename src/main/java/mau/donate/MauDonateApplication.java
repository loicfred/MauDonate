package mau.donate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MauDonateApplication {


    static void main(String[] args) {
        SpringApplication.run(MauDonateApplication.class, args);
    }

}
