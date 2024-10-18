package ru.isupden.vpnbot.util;

import org.apache.commons.text.RandomStringGenerator;

public class PromoCodeGenerator {
    private static final int LENGTH = 10;

    public static String generatePromoCode() {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange('A', 'Z')
                .build();
        return pwdGenerator.generate(LENGTH);
    }
}
