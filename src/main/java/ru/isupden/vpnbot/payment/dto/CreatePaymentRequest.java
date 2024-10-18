package ru.isupden.vpnbot.payment.dto;

import java.util.Map;

public record CreatePaymentRequest(
        Amount amount,
        String description,
        Map<String, String> metadata,
        Boolean capture,
        ConfirmationRequest confirmation
) {
}

