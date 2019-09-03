package br.pro.fagnerlima.spring.auth.api.infrastructure.service.exception;

public class MailException extends RuntimeException {

    private static final long serialVersionUID = 3414306611667430223L;

    public MailException(Throwable cause) {
        super(cause);
    }

}
