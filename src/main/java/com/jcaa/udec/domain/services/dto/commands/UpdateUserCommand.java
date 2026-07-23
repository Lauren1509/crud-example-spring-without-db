package com.jcaa.udec.domain.services.dto.commands;

import com.jcaa.udec.domain.exceptions.InvalidUserDataException;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;
import com.jcaa.udec.domain.valueobjects.UserName;

import java.util.Objects;

public record UpdateUserCommand(UserId userId, UserName name, EmailAddress email) {

    private static final String REQUIRED_FIELDS_MESSAGE = "Id, name and email are required to update a user";

    public UpdateUserCommand {
        if (Objects.isNull(userId) || Objects.isNull(name) || Objects.isNull(email)) {
            throw new InvalidUserDataException(REQUIRED_FIELDS_MESSAGE);
        }
    }
}
