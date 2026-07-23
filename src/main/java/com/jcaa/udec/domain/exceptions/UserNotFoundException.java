package com.jcaa.udec.domain.exceptions;

public final class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "The requested user does not exist";

    public UserNotFoundException() {
        super(MESSAGE);
    }
}
