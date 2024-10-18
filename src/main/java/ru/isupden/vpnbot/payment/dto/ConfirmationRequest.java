package ru.isupden.vpnbot.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfirmationRequest(
        String type,
        @JsonProperty("return_url")
        String returnUrl
) {
}
