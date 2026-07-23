package com.jcaa.udec.entrypoint.rest.v1.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String status,
        Instant createdAt,
        Instant updatedAt) {
}
