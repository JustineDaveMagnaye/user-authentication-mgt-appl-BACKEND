package com.rocs.user.exception.domain;

public class PersonExistsException extends Exception {
    public PersonExistsException(String message) {
        super(message);
    }
}
