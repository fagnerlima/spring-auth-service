package br.pro.fagnerlima.spring.auth.api.application.service.exception;

public class UsuarioBloqueadoException extends RuntimeException {

    private static final long serialVersionUID = 2416923864357521813L;

    public UsuarioBloqueadoException() {
        super();
    }

    public UsuarioBloqueadoException(String message) {
        super(message);
    }

}
