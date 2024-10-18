package ru.isupden.vpnbot.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isupden.vpnbot.db.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByTelegramId(Long telegramId);
    User findByPassword(String password);
    boolean existsByTelegramId(Long telegramId);
}
