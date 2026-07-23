package com.jcaa.udec.entrypoint.rest.v1.errors.handler;

import com.jcaa.udec.domain.exceptions.DuplicateUserException;
import com.jcaa.udec.domain.exceptions.InvalidUserDataException;
import com.jcaa.udec.domain.exceptions.UserNotFoundException;
import com.jcaa.udec.entrypoint.rest.v1.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

    private static final String INVALID_REQUEST_MESSAGE = "The request is invalid";
    private static final String INVALID_FIELD_MESSAGE = "Invalid field value";
    private static final String INTERNAL_ERROR_MESSAGE = "An unexpected error occurred";
    private static final String SANITIZED_EXCEPTION_MESSAGE = "Sanitized REST processing exception";
    private static final String LOG_UNEXPECTED_EXCEPTION = "Unexpected REST processing failure";

    private final Clock clock;

    public GlobalRestExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException exception,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                RestErrorCode.USER_NOT_FOUND,
                exception.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(DuplicateUserException.class)
    ResponseEntity<ApiErrorResponse> handleDuplicateUser(
            DuplicateUserException exception,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.CONFLICT,
                RestErrorCode.DUPLICATE_USER,
                exception.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(InvalidUserDataException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidUserData(
            InvalidUserDataException exception,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                RestErrorCode.INVALID_REQUEST,
                exception.getMessage(),
                request,
                Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                RestErrorCode.INVALID_REQUEST,
                INVALID_REQUEST_MESSAGE,
                request,
                extractValidationErrors(exception));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    ResponseEntity<ApiErrorResponse> handleMalformedRequest(HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                RestErrorCode.INVALID_REQUEST,
                INVALID_REQUEST_MESSAGE,
                request,
                Map.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception, HttpServletRequest request) {
        log.error(LOG_UNEXPECTED_EXCEPTION, sanitize(exception));
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                RestErrorCode.INTERNAL_ERROR,
                INTERNAL_ERROR_MESSAGE,
                request,
                Map.of());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            RestErrorCode code,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(clock),
                status.value(),
                status.getReasonPhrase(),
                code.name(),
                message,
                request.getRequestURI(),
                Map.copyOf(validationErrors));
        return ResponseEntity.status(status).body(response);
    }

    private static Map<String, String> extractValidationErrors(MethodArgumentNotValidException exception) {
        Function<FieldError, String> safeMessage = fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                .orElse(INVALID_FIELD_MESSAGE);
        return exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        safeMessage,
                        (firstMessage, ignoredMessage) -> firstMessage,
                        TreeMap::new));
    }

    private static RuntimeException sanitize(Exception exception) {
        RuntimeException sanitizedException = new RuntimeException(
                SANITIZED_EXCEPTION_MESSAGE + ": " + exception.getClass().getSimpleName());
        sanitizedException.setStackTrace(exception.getStackTrace());
        return sanitizedException;
    }
}
