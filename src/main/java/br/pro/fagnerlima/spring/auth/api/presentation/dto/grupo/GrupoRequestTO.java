package br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo;

import java.io.Serializable;
import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.converter.IdReference;

public class GrupoRequestTO implements Serializable {

    private static final long serialVersionUID = -4790281673432081607L;

    private String nome;

    @IdReference(target = Permissao.class, property = "permissoes")
    private Set<Long> permissoes;

    private Boolean ativo;

    public GrupoRequestTO() {
        super();
    }

    public GrupoRequestTO(String nome, Set<Long> permissoes, Boolean ativo) {
        super();
        this.nome = nome;
        this.permissoes = permissoes;
        this.ativo = ativo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Long> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(Set<Long> permissoes) {
        this.permissoes = permissoes;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return String.format("GrupoRequestTO [nome=%s, permissoes=%s, ativo=%s]", nome, permissoes, ativo);
    }

}
