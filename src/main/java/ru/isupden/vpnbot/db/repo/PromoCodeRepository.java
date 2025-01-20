package ru.isupden.vpnbot.db.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.isupden.vpnbot.db.entity.PromoCode;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, UUID> {
    boolean existsByPromoCode(String promoCode);
    PromoCode findByPromoCode(String promoCode);
    List<PromoCode> findAllByAssignedUserTelegramId(Long telegramId);
}
