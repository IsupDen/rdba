package ru.isupden.vpnbot.db.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isupden.vpnbot.db.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByTelegramId(Long telegramId);
    User findByVpnConfigurationPassword(String password);
    boolean existsByTelegramId(Long telegramId);
}
