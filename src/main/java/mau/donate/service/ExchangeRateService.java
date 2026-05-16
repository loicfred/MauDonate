package mau.donate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

public class ExchangeRateService {



//    public BigDecimal convertMurToUsd(BigDecimal murAmount) {
//        Map response = restClient.get()
//                .uri("https://api.frankfurter.dev/v1/latest?base=MUR&symbols=USD")
//                .retrieve()
//                .body(Map.class);
//        Map<String, Double> rates = (Map<String, Double>) response.get("rates");
//        BigDecimal rate = BigDecimal.valueOf(rates.get("USD"));
//        return murAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
//    }
}