package com.hasanalmunawr.booknetwork.exception;

public class OperationNotPermittedException extends RuntimeException{

    OperationNotPermittedException(String message) {
        super(message);
    }

    OperationNotPermittedException() {
        super("Operation UnPermitted");
    }


}
