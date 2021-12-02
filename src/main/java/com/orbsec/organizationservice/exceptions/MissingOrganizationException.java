package com.orbsec.organizationservice.exceptions;

public class MissingOrganizationException extends RuntimeException {
    public MissingOrganizationException() {
        super();
    }

    public MissingOrganizationException(String message) {
        super(message);
    }

    public MissingOrganizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingOrganizationException(Throwable cause) {
        super(cause);
    }

    protected MissingOrganizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
