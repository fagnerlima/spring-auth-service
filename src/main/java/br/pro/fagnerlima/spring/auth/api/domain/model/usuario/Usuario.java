package br.pro.fagnerlima.spring.auth.api.domain.model.usuario;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;

@Audited
@Entity
@Table(name = "usuario", schema = "auth")
public class Usuario extends BaseEntity<Long> {

    private static final long serialVersionUID = 4992180182377301896L;

    @NotNull
    @Size(min = 5, max = 128)
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull
    @Size(min = 5, max = 128)
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Embedded
    private Senha senha;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32)
    private Status status;

    @NotNull
    @NotEmpty
    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "t_usuario_grupo", schema = "autenticacao", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_grupo"))
    private List<Grupo> grupos;

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

    public Senha getSenha() {
        return senha;
    }

    public void setSenha(Senha senha) {
        this.senha = senha;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    @Override
    public String toString() {
        return String.format("Usuario [id=%s, nome=%s, email=%s, senha=%s, status=%s, grupos=%s]", id, nome, email, senha, status, grupos);
    }
}
