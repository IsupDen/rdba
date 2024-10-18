package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.isupden.vpnbot.bot.Bot;
import ru.isupden.vpnbot.payment.PaymentService;

@Component
public abstract class PaymentMessage extends Message {
    private final int countOfMonths;
    private final double multiplier;

    @Autowired
    private PaymentService paymentService;

    public PaymentMessage(int countOfMonths, double multiplier) {
        super(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Главная")
                                .callbackData("main")
                                .build())
                ),
                ""
        );
        this.countOfMonths = countOfMonths;
        this.multiplier = multiplier;
    }

    public PaymentMessage() {
        this(1, 1);
    }

    @Override
    public EditMessageText editMessage(Long chatId, Integer messageId) {
        var link = paymentService.createPayment(chatId, countOfMonths, multiplier);
        return Bot.editMessage(chatId, "[Ссылка для оплаты](%s)".formatted(link), messageId);
    }
}
