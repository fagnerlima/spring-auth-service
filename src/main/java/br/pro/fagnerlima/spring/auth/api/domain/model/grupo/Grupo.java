package br.pro.fagnerlima.spring.auth.api.domain.model.grupo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;

public class Grupo extends BaseEntity<Integer> {

    private static final long serialVersionUID = -7881272704210120357L;

    @NotNull
    @Size(min = 5, max = 128)
    @Column(name = "nome", length = 128, nullable = false)
    private String nome;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "grupo_permissao",
            schema = "auth",
            joinColumns = @JoinColumn(name = "id_grupo"),
            inverseJoinColumns = @JoinColumn(name = "id_permissao"))
    private List<Permissao> permissoes;

    @NotNull
    @Column(name = "ativo", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Permissao> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(List<Permissao> permissoes) {
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
        return String.format("Grupo [id=%s, nome=%s, permissoes=%s, ativo=%s]", id, nome, permissoes, ativo);
    }
}
