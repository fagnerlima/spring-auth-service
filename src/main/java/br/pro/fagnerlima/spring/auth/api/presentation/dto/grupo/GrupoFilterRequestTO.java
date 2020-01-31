package br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo;

import java.io.Serializable;
import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationField;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationOperation;

@SpecificationEntity(Grupo.class)
public class GrupoFilterRequestTO implements Serializable {

    private static final long serialVersionUID = 8652626915074598836L;

    @SpecificationField
    private Long id;

    @SpecificationField(property = "id")
    private Set<Long> ids;

    @SpecificationField(operation = SpecificationOperation.LIKE_IGNORE_CASE_UNACCENT)
    private String nome;

    @SpecificationField
    private Boolean ativo;

    public GrupoFilterRequestTO() {
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return String.format("GrupoFilterRequestTO [id=%s, ids=%s, nome=%s, ativo=%s]", id, ids, nome, ativo);
    }

}
