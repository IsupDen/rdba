package ru.isupden.vpnbot.db.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "subscription_plans", indexes = {
        @Index(name = "nameIndex", columnList = "name")
})
@NoArgsConstructor
public class SubscriptionPlan {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer durationMonths;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    public SubscriptionPlan(String name, Integer durationMonths, BigDecimal price) {
        this.name = name;
        this.durationMonths = durationMonths;
        this.price = price;
    }
}