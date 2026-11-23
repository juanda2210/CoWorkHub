package com.campuslands.coworkhub.business.exception;

public class InvalidDataException extends BusinessException {

    public InvalidDataException(String message) {
        super("VALIDATION_ERROR", message);
    }
}