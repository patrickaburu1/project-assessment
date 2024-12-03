package com.kcb.projectassessment.execption;

public class UnProcessingRequestException extends RuntimeException {
    public UnProcessingRequestException(String message) {
        super(message);
    }
}