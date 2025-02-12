package ru.isupden.vpnbot.db.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isupden.vpnbot.db.entity.MessageEntity;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    MessageEntity findByChatId(Long chatId);
}
