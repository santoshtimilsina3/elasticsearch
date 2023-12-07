package com.elk.exception;

import java.io.Serializable;

public class FailedToSaveException extends Exception implements Serializable {
    public FailedToSaveException() {
        super();
    }

    public FailedToSaveException(String message) {
        super(message);
    }

    public FailedToSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToSaveException(Throwable cause) {
        super(cause);
    }

    protected FailedToSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
