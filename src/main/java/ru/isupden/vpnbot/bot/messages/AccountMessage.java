package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.isupden.vpnbot.bot.Bot;
import ru.isupden.vpnbot.bot.messages.metadata.Metadata;
import ru.isupden.vpnbot.vpn.PromoCodeService;
import ru.isupden.vpnbot.vpn.VpnService;

@Component("account")
public class AccountMessage extends Message {
    @Autowired
    private VpnService vpnService;
    @Autowired
    private PromoCodeService promoCodeService;

    public AccountMessage() {
        super(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Оплата")
                                .callbackData("pay")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Бонус")
                                .callbackData("referral")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Главная")
                                .callbackData("main")
                                .build())
                ),
            """
                🔐 Ваша информация:
                
                •	Остаток дней подписки:
                %s
                •	Ваш ключ доступа:
                %s
                •	Активные промокоды:
                %s
                """
        );
    }

    @Override
    public EditMessageText editMessage(Metadata metadata) {
        var chatId = metadata.getChatId();
        var messageId = metadata.getMassageId();
        String accessKey = vpnService.getLink(chatId);
        if (accessKey == null) {
            return Bot.editMessage(chatId, "Чтобы получить код приобрети подписку", messageId);
        }
        return Bot.editMessage(
                chatId,
                text.formatted(
                      "`%s`".formatted(vpnService.getDays(chatId)),
                        "`%s`".formatted(accessKey),
                      String.join("\n", promoCodeService.getPromoCodes(chatId).stream().map("`%s`"::formatted).toList())
                ),
                messageId);
    }
}
