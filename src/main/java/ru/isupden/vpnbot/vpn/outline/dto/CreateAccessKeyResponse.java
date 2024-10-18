package ru.isupden.vpnbot.vpn.outline.dto;

public record CreateAccessKeyResponse(
        String id,
        String name,
        String password,
        int port,
        String method,
        String accessUrl
) {
}
