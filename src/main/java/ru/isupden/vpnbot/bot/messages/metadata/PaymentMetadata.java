package ru.isupden.vpnbot.bot.messages.metadata;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Builder(toBuilder = true)
@With
@Data
public class PaymentMetadata {
    private UUID subscriptionId;
    private BigDecimal price;
}
