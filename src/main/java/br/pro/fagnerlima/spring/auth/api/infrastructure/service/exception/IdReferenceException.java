package br.pro.fagnerlima.spring.auth.api.infrastructure.service.exception;

public class IdReferenceException extends RuntimeException {

    private static final long serialVersionUID = -3695357856217495262L;

    public IdReferenceException(Throwable cause) {
        super(cause);
    }

}
