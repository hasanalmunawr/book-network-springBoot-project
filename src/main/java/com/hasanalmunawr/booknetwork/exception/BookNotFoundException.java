package com.hasanalmunawr.booknetwork.exception;


public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException() {
        super("Book Not Found");
    }


}
