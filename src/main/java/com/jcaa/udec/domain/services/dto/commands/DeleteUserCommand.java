package com.jcaa.udec.domain.services.dto.commands;

import com.jcaa.udec.domain.exceptions.InvalidUserDataException;
import com.jcaa.udec.domain.valueobjects.UserId;

import java.util.Objects;

public record DeleteUserCommand(UserId userId) {

    private static final String REQUIRED_ID_MESSAGE = "User id is required to delete a user";

    public DeleteUserCommand {
        if (Objects.isNull(userId)) {
            throw new InvalidUserDataException(REQUIRED_ID_MESSAGE);
        }
    }
}
