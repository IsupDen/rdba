package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.isupden.vpnbot.bot.Bot;
import ru.isupden.vpnbot.db.repo.UserRepository;

@Component("pay")
public class PayMassage extends Message {
    @Autowired
    private UserRepository userRepository;

    public PayMassage() {
        super(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Главная")
                                .callbackData("main")
                                .build())
                ),
                "Выберите удобный для вас тариф, чтобы активировать или продлить доступ."
        );
    }

    @Override
    public EditMessageReplyMarkup editButtons(Long chatId, Integer massageId) {
        var user = userRepository.findByTelegramId(chatId);
        var price = user.getPrice();
        var newButtons = List.of(
                List.of(InlineKeyboardButton.builder()
                        .text("1 месяц - %s руб".formatted(price))
                        .callbackData("payment#1")
                        .build()),
                List.of(InlineKeyboardButton.builder()
                        .text("3 месяца - %s руб".formatted(price * 2.5))
                        .callbackData("payment#3")
                        .build()),
                List.of(InlineKeyboardButton.builder()
                        .text("12 месяцев - %s руб".formatted(price * 8))
                        .callbackData("payment#12")
                        .build()),
                List.of(InlineKeyboardButton.builder()
                        .text("Ввести промокод")
                        .callbackData("promocode")
                        .build()),
                List.of(InlineKeyboardButton.builder()
                        .text("Главная")
                        .callbackData("main")
                        .build())
        );
        return Bot.editButtons(chatId, new InlineKeyboardMarkup(newButtons), massageId);
    }
}
