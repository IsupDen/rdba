package ru.isupden.vpnbot.vpn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.isupden.vpnbot.db.entity.PromoCode;
import ru.isupden.vpnbot.db.entity.User;
import ru.isupden.vpnbot.db.repo.PromoCodeRepository;
import ru.isupden.vpnbot.db.repo.UserRepository;
import ru.isupden.vpnbot.vpn.dto.AccessKey;
import ru.isupden.vpnbot.vpn.outline.OutlineClient;
import ru.isupden.vpnbot.vpn.outline.dto.CreateAccessKeyRequest;
import ru.isupden.vpnbot.vpn.outline.dto.DataLimit;

@Service
public class VpnService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    @Autowired
    private OutlineClient outlineClient;
    @Value("${outline.server}")
    private String server;
    @Value("${outline.key-server}")
    private String keyServer;

    public void createUser(Long telegramId, String referralId, String name) {
        if (!userRepository.existsByTelegramId(telegramId)) {
            User referral = null;
            try {
                referral = userRepository.findByTelegramId(Long.parseLong(referralId));
            } catch (NumberFormatException ignored){
            }
            userRepository.save(new User(name, telegramId, referral));
        }
    }

    public String generateCode(Long telegramId) {
        var user = userRepository.findByTelegramId(telegramId);
        if (user.getAccessUrl() == null) {
            var accessKey = outlineClient.createUser(new CreateAccessKeyRequest(
                    user.getName(),
                    "chacha20-ietf-poly1305",
                    new DataLimit(50L * 1000 * 1000 * 1000)
            ));
            user.setAccessUrl(accessKey.accessUrl());
            user.setMethod(accessKey.method());
            user.setPort(accessKey.port());
            user.setPassword(accessKey.password());
            user.setServerIp(server);
            user.setKeyId(accessKey.id());
        }
        return "ssconf://%s/conf/%s".formatted(keyServer, user.getPassword());
    }

    public String getLink(Long telegramId) {
        if (!userRepository.existsByTelegramId(telegramId)) {
            return null;
        }
        var user = userRepository.findByTelegramId(telegramId);
        if (user.getAccessUrl() == null) {
            return null;
        }
        return "ssconf://%s/conf/%s".formatted(keyServer, user.getPassword());
    }

    public Long getDays(Long telegramId) {
        if (!userRepository.existsByTelegramId(telegramId)) {
            return null;
        }
        var user = userRepository.findByTelegramId(telegramId);
        return ChronoUnit.DAYS.between(LocalDate.now(), user.getExpirationDate().toLocalDate());
    }

    public List<String> getPromoCodes(Long telegramId) {
        if (!userRepository.existsByTelegramId(telegramId)) {
            return null;
        }
        var user = userRepository.findByTelegramId(telegramId);
        return promoCodeRepository.findAllByUserId(user.getId()).stream()
                .filter(promoCode -> promoCode.getExpirationDate().isAfter(LocalDateTime.now()))
                .map(PromoCode::getPromoCode)
                .toList();
    }

    public AccessKey getKey(String password) {
        var user = userRepository.findByPassword(password);
        return new AccessKey(
                user.getServerIp(),
                user.getPort(),
                user.getPassword(),
                user.getMethod()
        );
    }

    public void setDiscount(String promoCodeString, Long telegramId) {
        var promoCode = promoCodeRepository.findByPromoCode(promoCodeString);
        if (!promoCode.getUser().getTelegramId().equals(telegramId)) {
            return;
        }
        var user = promoCode.getUser();
        user.setPrice(user.getPrice() * (100 - promoCode.getDiscount()) / 100);
        userRepository.save(user);
        promoCodeRepository.delete(promoCode);
    }
}
