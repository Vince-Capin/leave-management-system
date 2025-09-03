package com.synacy.trainee.leavemanagementsystem.web.apierror;

import lombok.AccessLevel;
import lombok.Getter;

public class InvalidOperationException extends RuntimeException {
    @Getter(AccessLevel.PACKAGE)
    private final String errorCode;

    @Getter(AccessLevel.PACKAGE)
    private final String errorMessage;

    public InvalidOperationException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
