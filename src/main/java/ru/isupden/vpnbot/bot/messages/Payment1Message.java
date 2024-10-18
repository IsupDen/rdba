package ru.isupden.vpnbot.bot.messages;

import org.springframework.stereotype.Component;

@Component("payment#1")
public class Payment1Message extends PaymentMessage {
    public Payment1Message() {
        super(1, 1);
    }
}
