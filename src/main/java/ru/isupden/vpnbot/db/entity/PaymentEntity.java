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
@Table(name = "payments", indexes = {
        @Index(name = "idIndex", columnList = "paymentId")
})
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentId;
    @ManyToOne
    private User user;
    private Double amount;
    private LocalDateTime date;
    private Integer numberOfMonths;

    public PaymentEntity(User user, Double amount, Integer numberOfMonths) {
        this.user = user;
        this.amount = amount;
        this.numberOfMonths = numberOfMonths;
    }
}
