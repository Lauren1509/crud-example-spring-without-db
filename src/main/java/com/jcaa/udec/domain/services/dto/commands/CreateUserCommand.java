package com.jcaa.udec.domain.services.dto.commands;

import com.jcaa.udec.domain.exceptions.InvalidUserDataException;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserName;

import java.util.Objects;

public record CreateUserCommand(UserName name, EmailAddress email) {

    private static final String REQUIRED_FIELDS_MESSAGE = "Name and email are required to create a user";

    public CreateUserCommand {
        if (Objects.isNull(name) || Objects.isNull(email)) {
            throw new InvalidUserDataException(REQUIRED_FIELDS_MESSAGE);
        }
    }
}
