package ru.isupden.vpnbot.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.isupden.vpnbot.bot.messages.AccountMessage;
import ru.isupden.vpnbot.bot.messages.HelpMessage;
import ru.isupden.vpnbot.bot.messages.MainMessage;
import ru.isupden.vpnbot.bot.messages.Message;
import ru.isupden.vpnbot.bot.messages.PayMassage;
import ru.isupden.vpnbot.bot.messages.ReferralMessage;
import ru.isupden.vpnbot.bot.messages.metadata.Metadata;
import ru.isupden.vpnbot.db.repo.MessageRepository;
import ru.isupden.vpnbot.db.repo.PromoCodeRepository;
import ru.isupden.vpnbot.vpn.PromoCodeService;
import ru.isupden.vpnbot.vpn.VpnService;

@Getter
@Component
public class Bot extends TelegramLongPollingCommandBot {

    @Value("${bot.name}")
    private String botUsername;

    @Autowired
    private Set<BotCommand> commands;

    @Autowired
    private Map<String, Message> messages;

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private VpnService vpnService;

    @Autowired
    private PromoCodeService promoCodeService;

    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @PostConstruct
    private void registerCommands() {
        registerAll(commands.toArray(IBotCommand[]::new));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                var callData = update.getCallbackQuery().getData().split("#");
                var messageId = update.getCallbackQuery().getMessage().getMessageId();
                var chatId = update.getCallbackQuery().getMessage().getChatId();
                var metadata = Metadata.builder()
                        .chatId(chatId)
                        .massageId(messageId)
                        .metadata(callData.length > 1 ? callData[1] : null)
                        .build();
                var message = messages.get(callData[0]);
                var editMassage = message.editMessage(metadata);
                var editButtons = message.editButtons(metadata);
                execute(editMassage);
                if (!editButtons.getReplyMarkup().getKeyboard().isEmpty()) {
                    execute(editButtons);
                }
            } else if (update.hasMessage()) {
                //TODO вынести в отдельный сервис
                if (update.getMessage().getText().length() == 10 && promoCodeRepository.existsByPromoCode(update.getMessage().getText())) {
                    var chatId = update.getMessage().getChatId();
                    promoCodeService.activatePromoCode(update.getMessage().getText(), chatId);
                    var messageId = messageRepository.findByChatId(update.getMessage().getChatId()).getMessageId();
                    var message = messages.get("pay");
                    var metadata = Metadata.builder()
                            .chatId(chatId)
                            .massageId(messageId)
                            .build();
                    var editMassage = message.editMessage(metadata);
                    var editButtons = message.editButtons(metadata);
                    execute(editMassage);
                    execute(editButtons);
                    execute(new DeleteMessage(String.valueOf(chatId), update.getMessage().getMessageId()));
                } else {
                    execute(createMessage(update.getMessage().getChatId(), "Если у тебя возникли вопросы или нужна помощь, наша команда всегда готова поддержать! Свяжись с нами через @support, и мы быстро решим все твои запросы."));
                }
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static SendMessage createMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    public static EditMessageText editMessage(Long chatId, String text, Integer messageId) {
        return EditMessageText.builder()
                .chatId(chatId)
                .text(text)
                .messageId(messageId)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    public static EditMessageReplyMarkup editButtons(Long chatId, InlineKeyboardMarkup buttons, Integer messageId) {
        return EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .replyMarkup(buttons)
                .messageId(messageId)
                .build();
    }

    public static SendMessage createMessageWithButtons(Long chatId, String text, List<List<InlineKeyboardButton>> buttons) {
        var msg = createMessage(chatId, text);
        msg.setReplyMarkup(createKeyBoard(buttons));
        return msg;
    }

    private static ReplyKeyboard createKeyBoard(List<List<InlineKeyboardButton>> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(buttons.stream().map(rowButtons -> (List<InlineKeyboardButton>) new ArrayList<>(rowButtons)).toList())
                .build();
    }

    @Bean("messages")
    public Map<String, Message> getMessages() {
        return Map.of(
                "main", new MainMessage(),
                "help", new HelpMessage(),
                "account", new AccountMessage(),
                "referral", new ReferralMessage(),
                "pay", new PayMassage()
        );
    }
}
