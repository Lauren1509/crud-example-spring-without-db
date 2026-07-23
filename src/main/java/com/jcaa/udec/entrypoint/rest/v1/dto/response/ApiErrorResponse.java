package com.jcaa.udec.entrypoint.rest.v1.dto.response;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        Map<String, String> validationErrors) {
}
