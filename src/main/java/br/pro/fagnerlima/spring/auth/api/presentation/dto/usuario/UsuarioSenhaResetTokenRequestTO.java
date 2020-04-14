package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;

public class UsuarioSenhaResetTokenRequestTO implements Serializable {

    private static final long serialVersionUID = 1003205023105314425L;

    private String token;

    private String senha;

    public UsuarioSenhaResetTokenRequestTO() {
        super();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return String.format("UsuarioSenhaRequestTO [token=%s, senha=%s]", token, senha);
    }

}
