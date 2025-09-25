package com.example.bankcards.exception;

public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
