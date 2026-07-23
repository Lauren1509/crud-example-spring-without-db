package com.jcaa.udec.domain.valueobjects;

import com.jcaa.udec.domain.exceptions.InvalidUserDataException;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record EmailAddress(String value) {

    private static final int MAX_LENGTH = 254;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$",
            Pattern.CASE_INSENSITIVE);
    private static final String REQUIRED_MESSAGE = "User email is required";
    private static final String INVALID_MESSAGE = "User email is invalid";

    public EmailAddress {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new InvalidUserDataException(REQUIRED_MESSAGE);
        }
        String normalizedValue = value.trim().toLowerCase(Locale.ROOT);
        if (normalizedValue.length() > MAX_LENGTH || !EMAIL_PATTERN.matcher(normalizedValue).matches()) {
            throw new InvalidUserDataException(INVALID_MESSAGE);
        }
        value = normalizedValue;
    }

    public static EmailAddress of(String value) {
        return new EmailAddress(value);
    }
}
