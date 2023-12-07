package com.elk.exception;

import java.io.Serializable;

public class DatabaseOperationFailed extends RuntimeException implements Serializable {
    public DatabaseOperationFailed() {
        super();
    }

    public DatabaseOperationFailed(String message) {
        super(message);
    }

    public DatabaseOperationFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseOperationFailed(Throwable cause) {
        super(cause);
    }

    protected DatabaseOperationFailed(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
