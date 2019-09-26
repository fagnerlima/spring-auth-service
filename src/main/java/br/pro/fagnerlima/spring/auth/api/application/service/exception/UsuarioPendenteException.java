package br.pro.fagnerlima.spring.auth.api.application.service.exception;

public class UsuarioPendenteException extends RuntimeException {

    private static final long serialVersionUID = -4066135290886177726L;

    public UsuarioPendenteException() {
        super();
    }

    public UsuarioPendenteException(String message) {
        super(message);
    }

}
