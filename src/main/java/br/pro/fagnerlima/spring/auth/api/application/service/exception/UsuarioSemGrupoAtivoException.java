package br.pro.fagnerlima.spring.auth.api.application.service.exception;

public class UsuarioSemGrupoAtivoException extends RuntimeException {

    private static final long serialVersionUID = -4613231082193745982L;

    public UsuarioSemGrupoAtivoException() {
        super();
    }

    public UsuarioSemGrupoAtivoException(String message) {
        super(message);
    }

}
