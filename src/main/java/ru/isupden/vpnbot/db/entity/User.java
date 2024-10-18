package ru.isupden.vpnbot.db.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users", indexes = {
        @Index(name = "telegramIdIndex", columnList = "telegramId"),
        @Index(name = "passwordIndex", columnList = "password")
})
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private Long telegramId;
    private String name;
    private String keyId;
    private String serverIp;
    private String password;
    private Integer port;
    private String method;
    private String accessUrl;
    @ManyToOne
    private User referral;
    private LocalDateTime expirationDate;
    private Double price;

    public User(String name, Long telegramId, User referral) {
        this.name = name;
        this.telegramId = telegramId;
        this.referral = referral;
        this.price = 100d;
    }
}
