package ru.isupden.vpnbot.vpn;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.isupden.vpnbot.db.entity.User;
import ru.isupden.vpnbot.db.entity.VpnConfiguration;
import ru.isupden.vpnbot.db.repo.ServerRepository;
import ru.isupden.vpnbot.db.repo.UserRepository;
import ru.isupden.vpnbot.util.Validator;
import ru.isupden.vpnbot.vpn.dto.AccessKey;
import ru.isupden.vpnbot.vpn.outline.OutlineClient;
import ru.isupden.vpnbot.vpn.outline.dto.CreateAccessKeyRequest;
import ru.isupden.vpnbot.vpn.outline.dto.DataLimit;

@Service
@Slf4j
public class VpnService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OutlineClient outlineClient;

    @Autowired
    private ServerRepository serverRepository;

    @Value("${outline.server}")
    private String server;
    @Value("${outline.key-server}")
    private String keyServer;

    public void createUser(Long telegramId, String referralId, String name) {
        Validator.validateNotNull(telegramId, "Telegram ID cannot be null");
        Validator.validateNotEmpty(name, "Name cannot be empty");

        if (!userRepository.existsByTelegramId(telegramId)) {
            User referral = null;
            try {
                referral = userRepository.findByTelegramId(Long.parseLong(referralId));
            } catch (NumberFormatException ignored) {
                log.warn("Referral not found: {}", referralId);
            }
            var newUser = userRepository.save(new User(name, telegramId, referral));
            log.info("Created new user: {}", newUser);
        }
    }

    public User generateCode(User user) {
        Validator.validateNotNull(user, "User cannot be null");

        if (user.getVpnConfiguration() == null || user.getVpnConfiguration().getAccessUrl() == null) {
            if (user.getSubscriptionEndDate() == null || user.getSubscriptionEndDate().isBefore(LocalDateTime.now())) {
                return user;
            }
            var accessKey = outlineClient.createUser(new CreateAccessKeyRequest(
                    user.getName(),
                    "chacha20-ietf-poly1305",
                    new DataLimit(50L * 1000 * 1000 * 1000)
            ));
            log.info("Created access key: {}; telegramId: {}", accessKey, user.getTelegramId());
            var vpnConfiguration = new VpnConfiguration(accessKey.accessUrl(), accessKey.password(), accessKey.method(), accessKey.port(), server, accessKey.id());
            user.setVpnConfiguration(vpnConfiguration);
        }
        return user;
    }

    public String getLink(Long telegramId) {
        Validator.validateNotNull(telegramId, "Telegram ID cannot be null");

        if (!userRepository.existsByTelegramId(telegramId)) {
            return null;
        }
        var user = userRepository.findByTelegramId(telegramId);
        if (user.getVpnConfiguration() == null || user.getVpnConfiguration().getAccessUrl() == null) {
            return null;
        }
        return "ssconf://%s/conf/%s".formatted(keyServer, user.getVpnConfiguration().getPassword());
    }

    public Long getDays(Long telegramId) {
        Validator.validateNotNull(telegramId, "Telegram ID cannot be null");

        if (!userRepository.existsByTelegramId(telegramId)) {
            return null;
        }
        var user = userRepository.findByTelegramId(telegramId);
        return ChronoUnit.DAYS.between(LocalDate.now(), user.getSubscriptionEndDate().toLocalDate());
    }

    public AccessKey getKey(String password) {
        Validator.validateNotEmpty(password, "Password cannot be empty");

        var user = userRepository.findByVpnConfigurationPassword(password);
        if (user == null || user.getSubscriptionEndDate().isBefore(LocalDateTime.now())) {
            return null;
        }
        var serverIp = serverRepository.getTopByOrderByIdAsc().getIp();
        return new AccessKey(
                serverIp,
                user.getVpnConfiguration().getPort(),
                user.getVpnConfiguration().getPassword(),
                user.getVpnConfiguration().getMethod()
        );
    }
}
