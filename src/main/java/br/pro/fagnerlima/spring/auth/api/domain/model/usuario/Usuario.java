package br.pro.fagnerlima.spring.auth.api.domain.model.usuario;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;

//@Audited
@Entity
@Table(name = "usuario", schema = "auth")
public class Usuario extends BaseEntity {

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

    @Column(name = "pendente", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean pendente;

    @Column(name = "bloqueado", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean bloqueado;

    @NotNull
    @NotEmpty
    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "usuario_grupo",
            schema = "auth",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_grupo"))
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

    public Boolean getPendente() {
        return pendente;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setPendente(Boolean pendente) {
        this.pendente = pendente;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    @Override
    public String toString() {
        return String.format("Usuario [id=%s, nome=%s, email=%s, ativo=%s, pendente=%s, bloqueado=%s, grupos=%s]", id, nome, email, ativo,
                pendente, bloqueado, grupos);
    }
}
