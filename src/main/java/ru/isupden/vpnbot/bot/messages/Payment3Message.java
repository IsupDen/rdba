package ru.isupden.vpnbot.bot.messages;

import org.springframework.stereotype.Component;

@Component("payment#3")
public class Payment3Message extends PaymentMessage {
    public Payment3Message() {
        super(3, 2.5);
    }
}
