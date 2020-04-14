package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;

import javax.validation.constraints.Email;

public class UsuarioEmailRequestTO implements Serializable {

    private static final long serialVersionUID = 3538601138000055055L;

    @Email
    private String email;

    public UsuarioEmailRequestTO() {
        super();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("UsuarioEmailRequestTO [email=%s]", email);
    }

}
