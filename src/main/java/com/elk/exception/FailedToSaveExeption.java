package com.elk.exception;

public class FailedToSaveExeption extends Exception {
    public FailedToSaveExeption() {
        super();
    }

    public FailedToSaveExeption(String message) {
        super(message);
    }

    public FailedToSaveExeption(String message, Throwable cause) {
        super(message, cause);
    }

}
