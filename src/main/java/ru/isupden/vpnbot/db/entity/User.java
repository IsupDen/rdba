package ru.isupden.vpnbot.db.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "users", indexes = {
        @Index(name = "telegramIdIndex", columnList = "telegramId"),
})
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(unique = true, nullable = false)
    private Long telegramId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "referral_id")
    private User referral;

    private LocalDateTime subscriptionEndDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vpn_configuration_id")
    private VpnConfiguration vpnConfiguration;

    @ManyToOne
    @JoinColumn(name = "activated_promo_code_id")
    private PromoCode activatedPromoCode;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public User(String name, Long telegramId, User referral) {
        this.name = name;
        this.telegramId = telegramId;
        this.referral = referral;
    }
}
