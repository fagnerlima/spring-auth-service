package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;
import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.converter.IdReference;

public class UsuarioRequestTO implements Serializable {

    private static final long serialVersionUID = -7433561693465074511L;

    private String nome;

    private String email;

    private String login;

    @IdReference(target = Grupo.class, property = "grupos")
    private Set<Long> grupos;

    private Boolean ativo;

    public UsuarioRequestTO() {
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

    public Set<Long> getGrupos() {
        return grupos;
    }

    public void setGrupos(Set<Long> grupos) {
        this.grupos = grupos;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return String.format("UsuarioRequestTO [nome=%s, email=%s, login=%s, grupos=%s, ativo=%s]", nome, email, login, grupos, ativo);
    }

}
