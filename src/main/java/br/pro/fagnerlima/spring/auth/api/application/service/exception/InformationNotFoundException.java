package br.pro.fagnerlima.spring.auth.api.application.service.exception;

public class InformationNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -2371754895834756116L;

    public InformationNotFoundException() {
    }

    public InformationNotFoundException(String message) {
        super(message);
    }

}
