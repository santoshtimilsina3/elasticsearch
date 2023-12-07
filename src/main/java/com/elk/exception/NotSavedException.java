package com.elk.exception;

import java.io.Serializable;

public class NotSavedException extends Exception implements Serializable {
    public NotSavedException(String message) {
        super(message);
    }

    public NotSavedException() {
        super();
    }

    public NotSavedException(Throwable cause) {
        super(cause);
    }
}
