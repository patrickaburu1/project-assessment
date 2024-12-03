package com.kcb.projectassessment.execption;

public class UnAuthorizedRequestException extends RuntimeException {
    public UnAuthorizedRequestException(String message) {
        super(message);
    }
}