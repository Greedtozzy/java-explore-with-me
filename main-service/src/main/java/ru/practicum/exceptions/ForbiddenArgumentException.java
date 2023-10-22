package ru.practicum.exceptions;

public class ForbiddenArgumentException extends RuntimeException {
    public ForbiddenArgumentException(String message) {
        super(message);
    }
}
