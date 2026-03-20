package mau.donate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@SpringBootApplication
@ComponentScan({"mau.donate", "my.loic.utilities"})
public class MauDonateApplication {

    static void main(String[] args) {
        SpringApplication.run(MauDonateApplication.class, args);

        //System.err.println(aiService.prompt("Tell me about the Share The Way campaign", new CampaignTools()));

    }


}
