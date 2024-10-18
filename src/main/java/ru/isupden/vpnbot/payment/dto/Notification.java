package ru.isupden.vpnbot.payment.dto;

public record Notification(
        String type,
        String event,
        Payment object
) {
}
