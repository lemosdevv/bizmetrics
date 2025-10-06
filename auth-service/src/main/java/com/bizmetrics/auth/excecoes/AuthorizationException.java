package com.bizmetrics.auth.excecoes;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }

}
