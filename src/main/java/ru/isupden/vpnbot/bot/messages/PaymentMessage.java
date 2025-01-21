package ru.isupden.vpnbot.bot.messages;

import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.isupden.vpnbot.bot.Bot;
import ru.isupden.vpnbot.bot.messages.metadata.Metadata;
import ru.isupden.vpnbot.bot.messages.metadata.PaymentMetadata;
import ru.isupden.vpnbot.db.repo.MetadataRepository;
import ru.isupden.vpnbot.payment.PaymentService;

@Component("payment")
public class PaymentMessage extends Message {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private MetadataRepository metadataRepository;

    private final Gson gson = new Gson();

    public PaymentMessage() {
        super(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Главная")
                                .callbackData("main")
                                .build())
                ),
                ""
        );
    }

    @Override
    public EditMessageText editMessage(Metadata metadata) {
        if (metadata == null) {
            throw new IllegalArgumentException("Metadata cannot be null");
        }

        var metadataId = UUID.fromString(metadata.getMetadata());
        var paymentMetadata = gson.fromJson(
                metadataRepository.findById(metadataId).orElseThrow(() -> new IllegalArgumentException("Metadata not found for id: " + metadataId)).getMetadata(),
                PaymentMetadata.class
        );

        var chatId = metadata.getChatId();
        var messageId = metadata.getMassageId();

        if (chatId == null) {
            throw new IllegalArgumentException("ChatId cannot be null or empty");
        }

        if (messageId == null) {
            throw new IllegalArgumentException("MessageId cannot be null");
        }

        var link = paymentService.createPayment(chatId, paymentMetadata.getSubscriptionId(), paymentMetadata.getPrice());

        if (link == null || link.isEmpty()) {
            throw new IllegalStateException("Failed to create payment link");
        }

        return Bot.editMessage(chatId, "[Ссылка для оплаты](%s)".formatted(link), messageId);
    }
}
