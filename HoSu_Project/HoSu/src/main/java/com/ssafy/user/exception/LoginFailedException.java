package com.ssafy.user.exception;

// 로그인 실패용
public class LoginFailedException extends RuntimeException {
    public LoginFailedException(String message) {
        super(message);
    }
}
