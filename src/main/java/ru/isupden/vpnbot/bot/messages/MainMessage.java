package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component("main")
public class MainMessage extends Message {
    public MainMessage() {
        super(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Аккаунт")
                                .callbackData("account")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Оплата")
                                .callbackData("pay")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Помощь")
                                .callbackData("help")
                                .build())
                ),
            """
                Приветствуем! 🎉
                Здесь ты найдешь доступ к удобному инструменту для комфортного и безопасного интернет-серфинга.
                Мы предлагаем решения для стабильного и защищённого подключения к сети, чтобы ты всегда мог оставаться на связи с важными для тебя ресурсами.
                Если нужна помощь — просто напиши, мы всегда на связи!
                
                Это тестовый запуск, поэтому платить не нужно. Как только тестирование закончится, все балансы обнулятся. Всех тестировщиков обязательно наградим промокодами)
                """
        );
    }
}
