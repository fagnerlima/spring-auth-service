package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;

public class UsuarioAutenticadoRequestTO implements Serializable {

    private static final long serialVersionUID = -4976627326375118702L;

    private String nome;

    private String email;

    private String login;

    public UsuarioAutenticadoRequestTO() {
        super();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return String.format("UsuarioRequestTO [nome=%s, email=%s, login=%s]", nome, email, login);
    }

}
