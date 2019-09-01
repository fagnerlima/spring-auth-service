package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;
import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoResponseTO;

public class UsuarioResponseTO implements Serializable {

    private static final long serialVersionUID = -2629152547405312072L;

    private Long id;

    private String nome;

    private String email;

    private String login;

    private Boolean pendente;

    private Boolean bloqueado;

    private Boolean ativo;

    private Set<GrupoResponseTO> grupos;

    public UsuarioResponseTO() {
        super();
    }

    public UsuarioResponseTO(Long id, String nome, String email, String login, Boolean pendente, Boolean bloqueado, Boolean ativo,
            Set<GrupoResponseTO> grupos) {
        super();
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.pendente = pendente;
        this.bloqueado = bloqueado;
        this.ativo = ativo;
        this.grupos = grupos;
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

    public Set<GrupoResponseTO> getGrupos() {
        return grupos;
    }

    public void setGrupos(Set<GrupoResponseTO> grupos) {
        this.grupos = grupos;
    }

    @Override
    public String toString() {
        return String.format("UsuarioResponseTO [id=%s, nome=%s, email=%s, login=%s, pendente=%s, bloqueado=%s, ativo=%s, grupos=%s]", id,
                nome, email, login, pendente, bloqueado, ativo, grupos);
    }

}
