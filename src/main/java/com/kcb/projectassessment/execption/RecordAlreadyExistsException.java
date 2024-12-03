package com.kcb.projectassessment.execption;

public class RecordAlreadyExistsException extends RuntimeException {
    public RecordAlreadyExistsException(String message) {
        super(message);
    }
}