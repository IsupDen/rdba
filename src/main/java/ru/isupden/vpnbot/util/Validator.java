package ru.isupden.vpnbot.util;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;

@Slf4j
@UtilityClass
public class Validator {

    public void validateNotNull(Object object, String errorMessage) {
        if (Objects.isNull(object)) {
            log.warn("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void validateNotEmpty(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            log.warn("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void validateCondition(boolean condition, String errorMessage) {
        if (!condition) {
            log.warn("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void validateRange(Number number, Number min, Number max, String errorMessage) {
        if (number == null || number.doubleValue() < min.doubleValue() || number.doubleValue() > max.doubleValue()) {
            log.warn("Validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}