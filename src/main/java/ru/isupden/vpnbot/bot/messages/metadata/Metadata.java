package ru.isupden.vpnbot.bot.messages.metadata;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Builder(toBuilder = true)
@With
@Data
public class Metadata {
    private Long chatId;
    private Integer massageId;
    private String metadata;
}
