package ru.isupden.vpnbot.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.isupden.vpnbot.payment.dto.CreatePaymentRequest;
import ru.isupden.vpnbot.payment.dto.Payment;

@Service
public class PaymentClient {
    private final RestClient restClient;
    private final String url;
    private final String auth;

    public PaymentClient(@Value("${payment.url}")String url, @Value("${payment.shop-id}")String shopId, @Value("${payment.api-key}")String key) {
        this.restClient = RestClient.create();
        this.url = url;
        this.auth = "%s:%s".formatted(shopId, key);
    }

    public Payment createPayment(CreatePaymentRequest request, String idempotenceKey) {
        return restClient.post()
                .uri(url)
                .header("Idempotence-Key", idempotenceKey)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Payment.class);
    }
}
