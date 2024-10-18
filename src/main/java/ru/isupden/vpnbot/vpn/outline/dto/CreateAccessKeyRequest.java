package ru.isupden.vpnbot.vpn.outline.dto;

public record CreateAccessKeyRequest(
        String name,
        String method,
        DataLimit limit
) {
}
