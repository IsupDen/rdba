package ru.isupden.vpnbot.bot.commands;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.isupden.vpnbot.bot.messages.Message;
import ru.isupden.vpnbot.db.entity.MessageEntity;
import ru.isupden.vpnbot.db.repo.MessageRepository;
import ru.isupden.vpnbot.vpn.VpnService;

@Component
public class StartCommand extends BotCommand {

    @Autowired
    private VpnService vpnService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private Map<String, Message> messages;

    public StartCommand() {
        super(CommandName.START.name, "starting bot");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            vpnService.createUser(user.getId(), strings.length == 1 ? strings[0] : "", user.getUserName());
            var msg = absSender.execute(messages.get("main").getMessage(chat.getId()));
            var msgEntity = messageRepository.findByChatId(chat.getId());
            if (msgEntity == null) {
                msgEntity = new MessageEntity(msg.getMessageId(), chat.getId());
            } else {
                msgEntity.setMessageId(msg.getMessageId());
            }
            messageRepository.save(msgEntity);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}