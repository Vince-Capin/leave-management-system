package com.synacy.trainee.leavemanagementsystem.web.apierror;

public class InsufficientLeaveCreditsException extends RuntimeException {
    public InsufficientLeaveCreditsException(String message) {
        super(message);
    }
}
