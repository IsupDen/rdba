package ru.isupden.vpnbot.vpn;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.isupden.vpnbot.db.entity.PromoCode;
import ru.isupden.vpnbot.db.repo.PromoCodeRepository;

@Slf4j
@Service
public class PromoCodeService {
    @Autowired
    private PromoCodeRepository promoCodeRepository;

    public List<String> getPromoCodes(Long telegramId) {
        return promoCodeRepository.findAllByAssignedUserTelegramId(telegramId).stream()
                .filter(promoCode -> promoCode.getExpirationDate().isAfter(LocalDateTime.now()))
                .map(PromoCode::getPromoCode)
                .toList();
    }

    public void activatePromoCode(String promoCodeString, Long telegramId) {
        var promoCode = promoCodeRepository.findByPromoCode(promoCodeString);
        if (!promoCode.getAssignedUser().getTelegramId().equals(telegramId)) {
            //TODO придумать ошибку
            return;
        }
        if (promoCode.getExpirationDate().isAfter(LocalDateTime.now())) {
            return;
        }
        if (promoCode.getAssignedUser().getActivatedPromoCode() != null) {
            return;
        }
        promoCode.getAssignedUser().setActivatedPromoCode(promoCode);
        promoCodeRepository.save(promoCode);
        log.info("Activated promo code: {}; telegramId: {}", promoCode, telegramId);
    }
}
