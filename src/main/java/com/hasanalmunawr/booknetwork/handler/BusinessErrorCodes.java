package com.hasanalmunawr.booknetwork.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No Code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current Password Incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The Password Does Not Match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User Account Is Locked"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "User Account Is Disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Login And, Or Password Incorrect")
    ;


    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;


    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
