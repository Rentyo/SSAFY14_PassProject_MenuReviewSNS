package com.ssafy.user.exception;

public class DuplicatePhoneNumberException extends RuntimeException {
    public DuplicatePhoneNumberException(String message) {
        super(message);
    }
}