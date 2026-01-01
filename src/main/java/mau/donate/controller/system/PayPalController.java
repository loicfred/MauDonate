package mau.donate.controller.system;

import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import mau.donate.objects.Fundraising;
import mau.donate.objects.Notification;
import mau.donate.objects.User;
import mau.donate.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paypal")
public class PayPalController {

    private final PayPalHttpClient payPalClient;
    private final ExchangeRateService exchangeRateService;

    public PayPalController(PayPalHttpClient payPalClient, ExchangeRateService exchangeRateService) {
        this.payPalClient = payPalClient;
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping("/create-order")
    public Map<String, String> createOrder(Principal loggedUser, @RequestParam BigDecimal amountUsd) throws IOException {
        if (loggedUser == null) return null;
        //BigDecimal amountUsd = exchangeRateService.convertMurToUsd(amountMur);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode("USD").value(amountUsd.toString());

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .amountWithBreakdown(amount);

        orderRequest.purchaseUnits(List.of(purchaseUnit));

        OrdersCreateRequest request = new OrdersCreateRequest();
        request.requestBody(orderRequest);

        System.err.println(amountUsd.toString());
        Order order = payPalClient.execute(request).result();
        return Map.of("id", order.id()
                //, "amountMur", amountMur.toString()
                , "amountUsd", amountUsd.toString());
    }


    @PostMapping("/capture-order/{orderId}")
    public Map<String, Object> captureOrder(Principal loggedUser, @PathVariable String orderId, @RequestBody Map<String, Object> payload) throws IOException {
        if (loggedUser == null) return null;
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(new OrderRequest());

        Order order = payPalClient.execute(request).result();

        String amountUsd = order.purchaseUnits().getFirst().payments().captures().getFirst().amount().value();
        String typeUsd = order.purchaseUnits().getFirst().payments().captures().getFirst().amount().currencyCode();

        User U = User.getByEmail(loggedUser.getName());

        Fundraising fundraising = new Fundraising(U.getID(), Double.parseDouble(amountUsd), payload.get("title").toString(), payload.get("comment").toString());
        Notification notif = new Notification(U.getID(), "Successfully donated to fundraising!", "Transaction " + amountUsd + " " + typeUsd + " successfully done to fundraising.");

        return Map.of("status", order.status(), "id", order.id(), "amountUsd", amountUsd, "currencyUsd", typeUsd, "notification", notif);
    }
}
