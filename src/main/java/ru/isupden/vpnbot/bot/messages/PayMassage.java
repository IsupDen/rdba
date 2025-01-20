package ru.isupden.vpnbot.bot.messages;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.isupden.vpnbot.bot.Bot;
import ru.isupden.vpnbot.bot.messages.metadata.Metadata;
import ru.isupden.vpnbot.bot.messages.metadata.PaymentMetadata;
import ru.isupden.vpnbot.db.entity.MetadataEntity;
import ru.isupden.vpnbot.db.entity.PromoCode;
import ru.isupden.vpnbot.db.repo.MetadataRepository;
import ru.isupden.vpnbot.db.repo.SubscriptionRepository;
import ru.isupden.vpnbot.db.repo.UserRepository;

@Slf4j
@Component("pay")
public class PayMassage extends Message {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private MetadataRepository metadataRepository;
    private final Gson gson = new Gson();

    public PayMassage() {
        super(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Ввести промокод")
                                .callbackData("promocode")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Главная")
                                .callbackData("main")
                                .build())
                ),
                "Выберите удобный для вас тариф, чтобы активировать или продлить доступ."
        );
    }

    @Override
    public EditMessageReplyMarkup editButtons(Metadata metadata) {
        var chatId = metadata.getChatId();
        var messageId = metadata.getMassageId();

        var user = userRepository.findByTelegramId(chatId);
        var subscriptions = subscriptionRepository.findAll();
        var newButtons = new java.util.ArrayList<>(subscriptions.stream()
                .map(subscriptionPlan -> {
                    var price = calculatePrice(subscriptionPlan.getPrice(), user.getActivatedPromoCode());
                    var payMetadata = metadataRepository.save(new MetadataEntity(gson.toJson(PaymentMetadata.builder()
                            .subscriptionId(subscriptionPlan.getId())
                            .price(price)
                            .build())));
                    return List.of(InlineKeyboardButton.builder()
                            .text("%s - %s руб".formatted(subscriptionPlan.getName(), price))
                            .callbackData("payment#%s".formatted(payMetadata.getId()))
                            .build());
                })
                .toList());
        newButtons.addAll(buttons);

        return Bot.editButtons(chatId, new InlineKeyboardMarkup(newButtons), messageId);
    }

    private BigDecimal calculatePrice(BigDecimal price, @Nullable PromoCode promoCode) {
        if (promoCode == null) {
            return price;
        }
        if (price.multiply(BigDecimal.valueOf(promoCode.getDiscountAmount())).compareTo(promoCode.getMaxApplicableAmount()) < 0) {
            return price.multiply(BigDecimal.valueOf(1 - promoCode.getDiscountAmount()));
        }
        return price.subtract(promoCode.getMaxApplicableAmount());
    }
}
