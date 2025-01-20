package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.isupden.vpnbot.bot.Bot;
import ru.isupden.vpnbot.bot.messages.metadata.Metadata;

@Component("referral")
public class ReferralMessage extends Message {
    @Value("${bot.link}")
    private String link;

    public ReferralMessage() {
        super(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Аккаунт")
                                .callbackData("account")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Главная")
                                .callbackData("main")
                                .build())
                ),
            """
                 Приглашай друзей и получай бонусы! 🎁
                 За каждого приглашённого, который оформит подписку, тебе будет выдан промокод на 50% скидку на тот же период, который выбрал твой друг.
                 Больше приглашений — больше скидок! Делись своей персональной ссылкой и экономь вместе с нами!
                 """
        );
    }

    @Override
    public EditMessageText editMessage(Metadata metadata) {
        var chatId = metadata.getChatId();
        var messageId = metadata.getMassageId();
        return Bot.editMessage(chatId, text + "\n`%s?start=%s`".formatted(link, chatId), messageId);
    }
}
