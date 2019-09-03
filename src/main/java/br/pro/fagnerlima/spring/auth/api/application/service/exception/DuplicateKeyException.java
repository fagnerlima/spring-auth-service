package br.pro.fagnerlima.spring.auth.api.application.service.exception;

public class DuplicateKeyException extends RuntimeException {

    private static final long serialVersionUID = -1914234820108287912L;

    public DuplicateKeyException(String message) {
        super(message);
    }

}
