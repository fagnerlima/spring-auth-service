package br.pro.fagnerlima.spring.auth.api.application.service.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 4026908748344431668L;

    public BusinessException(String message) {
        super(message);
    }

}
