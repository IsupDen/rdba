package ru.isupden.vpnbot.payment.dto;

import java.util.Map;

public record Payment(
        String id,
        String status,
        Boolean paid,
        Map<String, String> metadata,
        Confirmation confirmation
) {
}

