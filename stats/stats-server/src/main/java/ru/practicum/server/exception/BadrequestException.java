package ru.practicum.server.exception;

public class BadrequestException extends RuntimeException {
    public BadrequestException(String message) {
        super(message);
    }
}
