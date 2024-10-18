package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component("help")
public class HelpMessage extends Message {
    public HelpMessage() {
        super(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Главная")
                                .callbackData("main")
                                .build())
                ),
                "Если у тебя возникли вопросы или нужна помощь, наша команда всегда готова поддержать! Свяжись с нами через @support, и мы быстро решим все твои запросы."
        );
    }
}
