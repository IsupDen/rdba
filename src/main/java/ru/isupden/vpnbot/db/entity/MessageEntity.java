package ru.isupden.vpnbot.db.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "chatIdIndex", columnList = "chatId")
})
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MessageEntity {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private Long chatId;

    @NotNull
    @Column(nullable = false)
    private Integer messageId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public MessageEntity(Integer messageId, Long chatId) {
        this.messageId = messageId;
        this.chatId = chatId;
    }
}
