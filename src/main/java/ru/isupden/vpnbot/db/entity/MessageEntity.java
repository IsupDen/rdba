package ru.isupden.vpnbot.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "chatIdIndex", columnList = "chatId")
})
@NoArgsConstructor
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private Integer messageId;

    public MessageEntity(Integer messageId, Long chatId) {
        this.messageId = messageId;
        this.chatId = chatId;
    }
}
