package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.isupden.vpnbot.bot.Bot;
import ru.isupden.vpnbot.bot.messages.metadata.Metadata;

import static ru.isupden.vpnbot.bot.Bot.createMessageWithButtons;


@AllArgsConstructor
public abstract class Message {
    protected List<List<InlineKeyboardButton>> buttons;
    protected String text;

    public SendMessage getMessage(Long chatId) {
        return createMessageWithButtons(chatId, text, buttons);
    }

    public EditMessageText editMessage(Metadata metadata) {
        return Bot.editMessage(metadata.getChatId(), text, metadata.getMassageId());
    }

    public EditMessageReplyMarkup editButtons(Metadata metadata) {
        return Bot.editButtons(metadata.getChatId(), new InlineKeyboardMarkup(buttons), metadata.getMassageId());
    }
}
