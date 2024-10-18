package ru.isupden.vpnbot.vpn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessKey(
        String server,
        @JsonProperty("server_port")
        int serverPort,
        String password,
        String method
) {
}
