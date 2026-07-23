package com.jcaa.udec.domain.services.dto.queries;

import com.jcaa.udec.domain.exceptions.InvalidUserDataException;
import com.jcaa.udec.domain.valueobjects.UserId;

import java.util.Objects;

public record GetUserQuery(UserId userId) {

    private static final String REQUIRED_ID_MESSAGE = "User id is required to get a user";

    public GetUserQuery {
        if (Objects.isNull(userId)) {
            throw new InvalidUserDataException(REQUIRED_ID_MESSAGE);
        }
    }
}
