package ru.isupden.vpnbot.vpn;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.isupden.vpnbot.bot.Bot;
import ru.isupden.vpnbot.db.repo.PromoCodeRepository;
import ru.isupden.vpnbot.db.repo.UserRepository;
import ru.isupden.vpnbot.vpn.outline.OutlineClient;
import ru.isupden.vpnbot.vpn.outline.dto.DataLimit;
import ru.isupden.vpnbot.vpn.outline.dto.SetLimitRequest;

import static ru.isupden.vpnbot.bot.Bot.createMessage;

@Service
public class Scheduler {
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OutlineClient outlineClient;
    @Autowired
    private Bot bot;

    @Scheduled(cron = "0 0 15 * * *")
    public void checkState() {
        promoCodeRepository.deleteAll(promoCodeRepository.findAll().stream()
                .filter(promoCode -> promoCode.getExpirationDate().isBefore(LocalDateTime.now()))
                .toList());
        userRepository.findAll().stream()
                .filter(user -> user.getSubscriptionEndDate() != null)
                .filter(user -> user.getSubscriptionEndDate().isBefore(LocalDateTime.now()))
                .forEach(user -> outlineClient.SetLimit(user.getVpnConfiguration().getKeyId(), new SetLimitRequest(new DataLimit(0))));
        userRepository.findAll().stream()
                .filter(user -> user.getSubscriptionEndDate() != null)
                .filter(user -> user.getSubscriptionEndDate().isBefore(LocalDateTime.now().plusDays(1)) && user.getSubscriptionEndDate().isAfter(LocalDateTime.now()))
                .forEach(user -> {
                    try {
                        bot.execute(createMessage(user.getTelegramId(), "Твоя подписка заканчивается завтра"));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
