package ru.isupden.vpnbot.db.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idIndex", columnList = "paymentId")
})
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(unique = true)
    private String paymentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "applied_promo_code_id")
    private PromoCode appliedPromoCode;

    public PaymentEntity(User user, BigDecimal amount, SubscriptionPlan subscriptionPlan) {
        this.user = user;
        this.amount = amount;
        this.subscriptionPlan = subscriptionPlan;
        this.appliedPromoCode = user.getActivatedPromoCode();
    }
}
