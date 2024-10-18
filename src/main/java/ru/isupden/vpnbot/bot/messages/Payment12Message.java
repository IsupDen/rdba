package ru.isupden.vpnbot.bot.messages;

import org.springframework.stereotype.Component;

@Component("payment#12")
public class Payment12Message extends PaymentMessage {
    public Payment12Message() {
        super(12, 8);
    }
}
