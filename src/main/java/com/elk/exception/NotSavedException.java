package com.elk.exception;

public class NotSavedException extends Exception {
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
