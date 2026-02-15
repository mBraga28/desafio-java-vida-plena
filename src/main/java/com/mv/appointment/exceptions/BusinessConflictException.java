package com.mv.appointment.exceptions;

public class BusinessConflictException extends RuntimeException{

    public BusinessConflictException(String message) {
        super(message);
    }
}
