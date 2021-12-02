package com.orbsec.organizationservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class CustomErrorHandler {

    private CustomError errorGenerator(Exception e, HttpStatus statusCode) {
        CustomError error = new CustomError();
        error.setErrorMessage(e.getMessage());
        error.setStatusCode(statusCode.value());
        error.setTimestamp(System.currentTimeMillis());
        return error;
    }

    @ExceptionHandler(MissingOrganizationException.class)
    public ResponseEntity<CustomError> organizationErrorHandler(MissingOrganizationException exception) {
        var error = errorGenerator(exception, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
