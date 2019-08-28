package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;

public class UsuarioReducedResponseTO implements Serializable {

    private static final long serialVersionUID = 1113904085583736516L;

    private Long id;

    private String nome;

    private String login;

    private Boolean pendente;

    private Boolean bloqueado;

    private Boolean ativo;

    public UsuarioReducedResponseTO() {
        super();
    }

    public UsuarioReducedResponseTO(Long id, String nome, String login, Boolean pendente, Boolean bloqueado, Boolean ativo) {
        super();
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.pendente = pendente;
        this.bloqueado = bloqueado;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Boolean getPendente() {
        return pendente;
    }

    public void setPendente(Boolean pendente) {
        this.pendente = pendente;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return String.format("UsuarioReducedResponseTO [id=%s, nome=%s, login=%s, pendente=%s, bloqueado=%s, ativo=%s]", id, nome, login,
                pendente, bloqueado, ativo);
    }

}
