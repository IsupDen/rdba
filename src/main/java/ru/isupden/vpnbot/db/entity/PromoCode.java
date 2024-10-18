package ru.isupden.vpnbot.db.entity;

import java.time.LocalDateTime;

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
@Table(name = "promocodes", indexes = {
        @Index(name = "promoCodeIndex", columnList = "promoCode"),
        @Index(name = "userIndex", columnList = "userId"),
})
@NoArgsConstructor
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String promoCode;
    private Integer discount;
    private LocalDateTime expirationDate;
    @ManyToOne
    private User user;

    public PromoCode(String promoCode, Integer discount, LocalDateTime expirationDate, User user) {
        this.promoCode = promoCode;
        this.discount = discount;
        this.expirationDate = expirationDate;
        this.user = user;
    }
}
