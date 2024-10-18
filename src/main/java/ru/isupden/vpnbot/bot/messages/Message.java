package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.isupden.vpnbot.bot.Bot;

import static ru.isupden.vpnbot.bot.Bot.createMessageWithButtons;


@AllArgsConstructor
public abstract class Message {
    protected List<List<InlineKeyboardButton>> buttons;
    protected String text;

    public SendMessage getMessage(Long chatId) {
        return createMessageWithButtons(chatId, text, buttons);
    }

    public EditMessageText editMessage(Long chatId, Integer messageId) {
        return Bot.editMessage(chatId, text, messageId);
    }

    public EditMessageReplyMarkup editButtons(Long chatId, Integer massageId) {
        return Bot.editButtons(chatId, new InlineKeyboardMarkup(buttons), massageId);
    }
}
