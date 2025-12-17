package mau.donate.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class ExchangeRateService {

    private final RestClient restClient;

    public ExchangeRateService(RestClient restClient) {
        this.restClient = restClient;
    }

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