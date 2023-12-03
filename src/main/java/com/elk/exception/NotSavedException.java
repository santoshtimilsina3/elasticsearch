package com.elk.exception;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotSavedExceptionMapper extends Exception {
    public NotSavedExceptionMapper(String message) {
        super(message);
    }

    public NotSavedExceptionMapper() {
        super();
    }

    public NotSavedExceptionMapper(Throwable cause) {
        super(cause);
    }
}
