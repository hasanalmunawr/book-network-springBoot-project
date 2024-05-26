package com.hasanalmunawr.booknetwork.exception;


public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException() {
        super("Entity Is Not Found");
    }


}
