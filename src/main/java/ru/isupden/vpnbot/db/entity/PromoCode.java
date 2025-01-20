package ru.isupden.vpnbot.db.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "promocodes", indexes = {
        @Index(name = "promoCodeIndex", columnList = "promoCode"),
        @Index(name = "assignedUserIndex", columnList = "assigned_user_telegram_id"),
})
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PromoCode {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String promoCode;

    @NotNull
    @Column(nullable = false)
    private Double discountAmount;

    @NotNull
    @Column(nullable = false)
    private BigDecimal maxApplicableAmount;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @ManyToOne
    @JoinColumn(name = "assigned_user_telegram_id", referencedColumnName = "telegramId")
    private User assignedUser;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PromoCode(String code, Double discount, BigDecimal maxApplicableAmount, LocalDateTime expirationDate, User assignedUser) {
        this.promoCode = code;
        this.discountAmount = discount;
        this.maxApplicableAmount = maxApplicableAmount;
        this.expirationDate = expirationDate;
        this.assignedUser = assignedUser;
    }
}
