package br.pro.fagnerlima.spring.auth.api.domain.model.grupo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;

//@Audited
@Entity
@Table(name = "grupo", schema = "auth")
public class Grupo extends BaseEntity {

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

    @Override
    public String toString() {
        return String.format("Grupo [id=%s, nome=%s, permissoes=%s, ativo=%s]", id, nome, permissoes, ativo);
    }
}
