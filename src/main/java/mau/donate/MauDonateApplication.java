package mau.donate;

import mau.donate.objects.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import static mau.donate.config.AppConfig.cacheService;

@EnableCaching
@SpringBootApplication
public class MauDonateApplication {


    static void main(String[] args) {
        SpringApplication.run(MauDonateApplication.class, args);

        System.out.println(User.getById(1));
        System.out.println(User.getById(1));
        System.out.println(User.getById(1));
        System.out.println(User.getById(1));
    }

}
