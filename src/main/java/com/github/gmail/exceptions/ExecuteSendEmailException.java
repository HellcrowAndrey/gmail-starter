package com.github.gmail.exceptions;

public class ExecuteSendEmailException extends RuntimeException {

    public ExecuteSendEmailException() {
    }

    public ExecuteSendEmailException(String message) {
        super(message);
    }
}
