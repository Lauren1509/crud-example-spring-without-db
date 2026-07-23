package com.jcaa.udec.domain.services.dto.response;

import com.jcaa.udec.domain.enums.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResult(
        UUID id,
        String name,
        String email,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
