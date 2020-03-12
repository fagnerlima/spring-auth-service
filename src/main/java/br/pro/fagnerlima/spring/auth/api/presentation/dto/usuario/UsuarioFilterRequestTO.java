package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;
import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationField;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.Operation;

public class UsuarioFilterRequestTO implements Serializable {

    private static final long serialVersionUID = 7902233005816840462L;

    private Long id;

    @SpecificationField(property = "id")
    private Set<Long> ids;

    @SpecificationField(operation = Operation.LIKE_IGNORE_CASE_UNACCENT)
    private String nome;

    @SpecificationField(operation = Operation.LIKE_IGNORE_CASE)
    private String email;

    @SpecificationField(operation = Operation.LIKE_IGNORE_CASE)
    private String login;

    private Boolean ativo;

    public UsuarioFilterRequestTO() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return String.format("UsuarioFilterRequestTO [id=%s, nome=%s, email=%s, login=%s, ativo=%s]", id, nome, email, login, ativo);
    }

}
