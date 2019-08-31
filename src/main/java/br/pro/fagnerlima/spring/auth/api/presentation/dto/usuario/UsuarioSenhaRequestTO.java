package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;

public class UsuarioSenhaRequestTO implements Serializable {

    private static final long serialVersionUID = -2750268280690970610L;

    private String senhaAtual;

    private String senhaNova;

    public UsuarioSenhaRequestTO() {
        super();
    }

    public UsuarioSenhaRequestTO(String senhaAtual, String senhaNova) {
        super();
        this.senhaAtual = senhaAtual;
        this.senhaNova = senhaNova;
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getSenhaNova() {
        return senhaNova;
    }

    public void setSenhaNova(String senhaNova) {
        this.senhaNova = senhaNova;
    }

    @Override
    public String toString() {
        return String.format("UsuarioSenhaRequestTO [senhaAtual=%s, senhaNova=%s]", senhaAtual, senhaNova);
    }

}
