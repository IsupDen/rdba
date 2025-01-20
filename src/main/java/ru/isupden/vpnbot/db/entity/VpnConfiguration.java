package ru.isupden.vpnbot.db.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "vpn_configurations", indexes = {
        @Index(name = "passwordIndex", columnList = "password"),
})
@NoArgsConstructor
public class VpnConfiguration {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String keyId;

    @NotNull
    @Column(nullable = false)
    private String serverIp;

    @NotNull
    @Column(nullable = false)
    private Integer port;

    @NotNull
    @Column(nullable = false)
    private String method;

    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
    @Column(nullable = false)
    private String accessUrl;

    public VpnConfiguration(String accessUrl, String password, String method, Integer port, String serverIp, String keyId) {
        this.accessUrl = accessUrl;
        this.password = password;
        this.method = method;
        this.port = port;
        this.serverIp = serverIp;
        this.keyId = keyId;
    }
}
