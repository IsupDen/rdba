package ru.isupden.vpnbot.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Confirmation(
        String type,
        @JsonProperty("confirmation_url")
        String confirmationUrl
) {
}
