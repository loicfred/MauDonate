package mau.donate.controller.system;

import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import mau.donate.objects.Fundraising;
import mau.donate.objects.Notification;
import mau.donate.objects.User;
import mau.donate.objects.enums.PaymentStatus;
import mau.donate.service.ExchangeRateService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PreAuthorize("isAuthenticated()")
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

        Order order = payPalClient.execute(request).result();
        return Map.of("id", order.id()
                //, "amountMur", amountMur.toString()
                , "amountUsd", amountUsd.toString());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/capture-order/{orderId}")
    public Map<String, Object> captureOrder(Principal loggedUser, @PathVariable String orderId, @RequestBody Map<String, Object> payload) throws IOException {
        if (loggedUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
        User U = User.getByEmail(loggedUser.getName());
        Fundraising fundraising = new Fundraising(orderId, U.getID(), 0, payload.get("title").toString(), payload.get("comment").toString());
        try {
            Order order = payPalClient.execute(new OrdersCaptureRequest(orderId).requestBody(new OrderRequest())).result();
            String amountUsd = order.purchaseUnits().getFirst().payments().captures().getFirst().amount().value();
            String typeUsd = order.purchaseUnits().getFirst().payments().captures().getFirst().amount().currencyCode();

            fundraising.USD = Double.parseDouble(amountUsd);
            Notification notif = new Notification(U.getID(), "Successfully donated to fundraising!", "Transaction " + amountUsd + " " + typeUsd + " successfully done to fundraising.");
            fundraising.Status = PaymentStatus.COMPLETED.name();
            return Map.of("status", order.status(), "id", order.id(), "amountUsd", amountUsd, "currencyUsd", typeUsd, "notification", notif);
        } catch (Exception e) {
            fundraising.Status = PaymentStatus.ERROR.name();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
        } finally {
            fundraising.Update();
        }
    }
}
