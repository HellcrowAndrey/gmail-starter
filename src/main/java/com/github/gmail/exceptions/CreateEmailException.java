package com.github.gmail.exceptions;

public class CreateEmailException extends RuntimeException {
    public CreateEmailException() {
    }

    public CreateEmailException(String message) {
        super(message);
    }
}
