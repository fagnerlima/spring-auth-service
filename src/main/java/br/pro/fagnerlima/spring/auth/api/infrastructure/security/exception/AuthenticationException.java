package br.pro.fagnerlima.spring.auth.api.infrastructure.security.exception;

public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 2110209492483467302L;

    public AuthenticationException(String message) {
        super(message);
    }

}
