package br.pro.fagnerlima.spring.auth.api.domain.model.permissao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "permissao", schema = "auth")
public class Permissao extends BaseEntity {

    private static final long serialVersionUID = -93989467728114655L;

    public static final Long ID_ROOT = -1L;
    public static final Long ID_SYSTEM = -2L;
    public static final Long ID_ADMIN = 1L;

    @NotNull
    @Size(min = 3, max = 128)
    @Enumerated(EnumType.STRING)
    @Column(name = "papel", length = 128, nullable = false)
    private Papel papel;

    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "descricao", length = 128, nullable = false)
    private String descricao;

    public Boolean isRoot() {
        return id != null && id.equals(ID_ROOT);
    }

    public Boolean isSystem() {
        return id != null && id.equals(ID_SYSTEM);
    }

    public Boolean isAdmin() {
        return id != null && id.equals(ID_ADMIN);
    }

    public Boolean hasRoot() {
        return papel != null && papel.isRoot();
    }

    public Boolean hasSystem() {
        return papel != null && papel.isSystem();
    }

    public Boolean hasAdmin() {
        return papel != null && papel.isAdmin();
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return String.format("Permissao [id=%s, papel=%s, descricao=%s]", id, papel, descricao);
    }
}
