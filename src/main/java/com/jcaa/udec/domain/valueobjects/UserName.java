package com.jcaa.udec.domain.valueobjects;

import com.jcaa.udec.domain.exceptions.InvalidUserDataException;

import java.util.Objects;
import java.util.regex.Pattern;

public record UserName(String value) {

    private static final int MAX_LENGTH = 100;
    private static final Pattern REPEATED_WHITESPACE = Pattern.compile("\\s+");
    private static final String REQUIRED_MESSAGE = "User name is required";
    private static final String LENGTH_MESSAGE = "User name must contain at most 100 characters";

    public UserName {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new InvalidUserDataException(REQUIRED_MESSAGE);
        }
        String normalizedValue = REPEATED_WHITESPACE.matcher(value.trim()).replaceAll(" ");
        if (normalizedValue.length() > MAX_LENGTH) {
            throw new InvalidUserDataException(LENGTH_MESSAGE);
        }
        value = normalizedValue;
    }

    public static UserName of(String value) {
        return new UserName(value);
    }
}
