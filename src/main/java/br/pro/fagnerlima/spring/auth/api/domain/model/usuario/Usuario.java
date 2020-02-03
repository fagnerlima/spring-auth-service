package br.pro.fagnerlima.spring.auth.api.domain.model.usuario;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.shared.AuditedBaseEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.listener.UsuarioListener;

@EntityListeners(UsuarioListener.class)
@Audited
@Entity
@Table(name = "usuario", schema = "auth")
public class Usuario extends AuditedBaseEntity {

    private static final long serialVersionUID = 4992180182377301896L;

    public static final Long ID_ADMIN = 1L;

    @NotNull
    @Size(min = 5, max = 128)
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull
    @Size(min = 5, max = 128)
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(min = 5, max = 32)
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Valid
    @Embedded
    private Senha senha;

    @NotNull
    @Column(name = "pendente", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean pendente;

    @NotNull
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
    private Set<Grupo> grupos;

    public Usuario() {
        super();
        pendente = true;
        bloqueado = false;
    }

    public Usuario(@NotNull @Size(min = 5, max = 128) String nome, @NotNull @Size(min = 5, max = 128) @Email String email,
            @NotNull @Size(min = 5, max = 32) String login, @Valid Senha senha, @NotNull @NotEmpty Set<Grupo> grupos) {
        this();
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;
        this.grupos = grupos;
    }

    public Usuario(@NotNull @Size(min = 5, max = 128) String nome, @NotNull @Size(min = 5, max = 128) @Email String email,
            @NotNull @Size(min = 5, max = 32) String login, @Valid Senha senha, @NotNull Boolean pendente, @NotNull Boolean bloqueado,
            @NotNull @NotEmpty Set<Grupo> grupos) {
        this();
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;
        this.pendente = pendente;
        this.bloqueado = bloqueado;
        this.grupos = grupos;
    }

    public Boolean isAdmin() {
        return id != null && id.equals(ID_ADMIN);
    }

    public void bloquear() {
        setBloqueado(true);
    }

    public void updateSenha(String senha) {
        this.senha.setValor(senha);
        this.senha.clearResetToken();
        this.senha.setDataAtualizacao(LocalDateTime.now());

        if (pendente) {
            pendente = false;
        }

        if (bloqueado) {
            bloqueado = false;
        }
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

    public Senha getSenha() {
        return senha;
    }

    public void setSenha(Senha senha) {
        this.senha = senha;
    }

    public Boolean getPendente() {
        return pendente;
    }

    public void setPendente(Boolean pendente) {
        this.pendente = pendente;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Set<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(Set<Grupo> grupos) {
        this.grupos = grupos;
    }

    @Override
    public String toString() {
        return String.format("Usuario [id=%s, nome=%s, email=%s, login=%s, ativo=%s, pendente=%s, bloqueado=%s, grupos=%s]", id, nome,
                email, login, ativo, pendente, bloqueado, grupos);
    }
}
