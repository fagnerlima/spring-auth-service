package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;

public class UsuarioSenhaResetTokenRequestTO implements Serializable {

    private static final long serialVersionUID = 1003205023105314425L;

    private String resetToken;

    private String senha;

    public UsuarioSenhaResetTokenRequestTO() {
        super();
    }

    public UsuarioSenhaResetTokenRequestTO(String resetToken, String senha) {
        super();
        this.resetToken = resetToken;
        this.senha = senha;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return String.format("UsuarioSenhaRequestTO [resetToken=%s, senha=%s]", resetToken, senha);
    }

}
