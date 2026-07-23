package com.jcaa.udec.domain.exceptions;

public final class DuplicateUserException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "A user with the supplied email already exists";

    public DuplicateUserException() {
        super(MESSAGE);
    }
}
