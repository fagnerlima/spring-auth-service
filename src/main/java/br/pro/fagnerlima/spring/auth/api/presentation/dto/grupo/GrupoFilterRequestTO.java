package br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo;

import java.io.Serializable;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationField;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationOperation;

@SpecificationEntity(Grupo.class)
public class GrupoFilterRequestTO implements Serializable {

    private static final long serialVersionUID = 8652626915074598836L;

    @SpecificationField
    private Long id;

    @SpecificationField(operation = SpecificationOperation.LIKE_IGNORE_CASE)
    private String nome;

    @SpecificationField
    private Boolean ativo;

    public GrupoFilterRequestTO() {
        super();
    }

    public GrupoFilterRequestTO(Long id, String nome, Boolean ativo) {
        super();
        this.id = id;
        this.nome = nome;
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return String.format("GrupoFilterRequestTO [id=%s, nome=%s, ativo=%s]", id, nome, ativo);
    }

}
