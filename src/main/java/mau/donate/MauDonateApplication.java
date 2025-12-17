package mau.donate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

//@EnableCaching
@SpringBootApplication
public class MauDonateApplication {


    static void main(String[] args) {
        SpringApplication.run(MauDonateApplication.class, args);
    }

}
