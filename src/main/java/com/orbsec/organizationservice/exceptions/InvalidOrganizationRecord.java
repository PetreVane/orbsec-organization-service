package com.orbsec.organizationservice.exceptions;

public class InvalidOrganizationRecord extends RuntimeException{
    public InvalidOrganizationRecord() {
        super();
    }

    public InvalidOrganizationRecord(String message) {
        super(message);
    }

    public InvalidOrganizationRecord(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOrganizationRecord(Throwable cause) {
        super(cause);
    }

    protected InvalidOrganizationRecord(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
