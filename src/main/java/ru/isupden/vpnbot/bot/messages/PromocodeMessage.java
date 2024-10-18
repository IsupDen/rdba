package ru.isupden.vpnbot.bot.messages;

import java.util.List;

import org.springframework.stereotype.Component;

@Component("promocode")
public class PromocodeMessage extends Message {
    public PromocodeMessage() {
        super(List.of(), "Введи промокод");
    }
}
