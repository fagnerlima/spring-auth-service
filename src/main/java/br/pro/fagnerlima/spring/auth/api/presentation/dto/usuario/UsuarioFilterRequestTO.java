package br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario;

import java.io.Serializable;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationField;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationOperation;

@SpecificationEntity(Usuario.class)
public class UsuarioFilterRequestTO implements Serializable {

    private static final long serialVersionUID = 7902233005816840462L;

    @SpecificationField
    private Long id;

    @SpecificationField(operation = SpecificationOperation.LIKE_IGNORE_CASE)
    private String nome;

    @SpecificationField(operation = SpecificationOperation.LIKE_IGNORE_CASE)
    private String email;

    @SpecificationField(operation = SpecificationOperation.LIKE_IGNORE_CASE)
    private String login;

    @SpecificationField
    private Boolean ativo;

    public UsuarioFilterRequestTO() {
        super();
    }

    public UsuarioFilterRequestTO(Long id, String nome, String email, String login, Boolean ativo) {
        super();
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.login = login;
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
