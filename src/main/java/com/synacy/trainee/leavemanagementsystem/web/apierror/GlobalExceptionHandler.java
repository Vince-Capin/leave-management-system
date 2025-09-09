package com.synacy.trainee.leavemanagementsystem.web.apierror;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsNotFoundException;
import com.synacy.trainee.leavemanagementsystem.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserNotFoundException.class})
    public ApiErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ApiErrorResponse("USER_NOT_FOUND", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(InsufficientLeaveCreditsException.class)
    public ApiErrorResponse handleInsufficientLeaveCredits(InsufficientLeaveCreditsException e) {
        return new ApiErrorResponse("INSUFFICIENT_CREDITS", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InvalidOperationException.class)
    public ApiErrorResponse handleInvalidOperationException(InvalidOperationException e) {
        return new ApiErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

}
