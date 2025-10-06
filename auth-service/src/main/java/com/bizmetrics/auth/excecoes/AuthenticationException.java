package com.bizmetrics.auth.excecoes;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }

}
