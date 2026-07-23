package com.jcaa.udec.domain.valueobjects;

import com.jcaa.udec.domain.exceptions.InvalidUserDataException;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {

    private static final String REQUIRED_MESSAGE = "User id is required";

    public UserId {
        if (Objects.isNull(value)) {
            throw new InvalidUserDataException(REQUIRED_MESSAGE);
        }
    }

    public static UserId from(UUID value) {
        return new UserId(value);
    }

    public static UserId random() {
        return new UserId(UUID.randomUUID());
    }
}
