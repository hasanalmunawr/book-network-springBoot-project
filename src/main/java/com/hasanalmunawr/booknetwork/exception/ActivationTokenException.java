package com.hasanalmunawr.booknetwork.exception;

public class ActivationTokenException extends RuntimeException{

    ActivationTokenException(String message) {
        super(message);
    }
    ActivationTokenException() {
        super("Token Invalid");
    }

}
