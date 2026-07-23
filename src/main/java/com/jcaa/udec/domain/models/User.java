package com.jcaa.udec.domain.models;

import com.jcaa.udec.domain.enums.UserStatus;
import com.jcaa.udec.domain.exceptions.InvalidUserDataException;
import com.jcaa.udec.domain.valueobjects.EmailAddress;
import com.jcaa.udec.domain.valueobjects.UserId;
import com.jcaa.udec.domain.valueobjects.UserName;

import java.time.Instant;
import java.util.Objects;

public final class User {

    private static final String REQUIRED_FIELDS_MESSAGE = "All user fields are required";
    private static final String INVALID_TIMESTAMPS_MESSAGE = "User update time cannot precede creation time";

    private final UserId id;
    private final UserName name;
    private final EmailAddress email;
    private final UserStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(
            UserId id,
            UserName name,
            EmailAddress email,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt) {
        validateRequiredFields(id, name, email, status, createdAt, updatedAt);
        if (updatedAt.isBefore(createdAt)) {
            throw new InvalidUserDataException(INVALID_TIMESTAMPS_MESSAGE);
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(UserId id, UserName name, EmailAddress email, Instant creationTime) {
        return new User(id, name, email, UserStatus.ACTIVE, creationTime, creationTime);
    }

    public User update(UserName updatedName, EmailAddress updatedEmail, Instant updateTime) {
        return new User(id, updatedName, updatedEmail, status, createdAt, updateTime);
    }

    public UserId getId() {
        return id;
    }

    public UserName getName() {
        return name;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private static void validateRequiredFields(
            UserId id,
            UserName name,
            EmailAddress email,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt) {
        if (Objects.isNull(id)
                || Objects.isNull(name)
                || Objects.isNull(email)
                || Objects.isNull(status)
                || Objects.isNull(createdAt)
                || Objects.isNull(updatedAt)) {
            throw new InvalidUserDataException(REQUIRED_FIELDS_MESSAGE);
        }
    }
}
