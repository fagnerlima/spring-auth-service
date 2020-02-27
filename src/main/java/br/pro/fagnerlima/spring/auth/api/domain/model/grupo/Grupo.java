package br.pro.fagnerlima.spring.auth.api.domain.model.grupo;

import java.util.Set;

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
import org.hibernate.envers.Audited;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.shared.AuditedBaseEntity;

@Audited
@Entity
@Table(name = "grupo", schema = "auth")
public class Grupo extends AuditedBaseEntity {

    private static final long serialVersionUID = -7881272704210120357L;

    public static final Long ID_ROOT = -1L;
    public static final Long ID_SYSTEM = -2L;
    public static final Long ID_ADMIN = 1L;

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
    private Set<Permissao> permissoes;

    public Grupo() {
        super();
    }

    public Boolean isRoot() {
        return id != null && id.equals(ID_ROOT);
    }

    public Boolean isSystem() {
        return id != null && id.equals(ID_SYSTEM);
    }

    public Boolean isAdmin() {
        return id != null && id.equals(ID_ADMIN);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Permissao> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(Set<Permissao> permissoes) {
        this.permissoes = permissoes;
    }

    @Override
    public String toString() {
        return String.format("Grupo [id=%s, nome=%s, permissoes=%s, ativo=%s]", id, nome, permissoes, ativo);
    }
}
