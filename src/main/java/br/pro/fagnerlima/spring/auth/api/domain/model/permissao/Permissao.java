package br.pro.fagnerlima.spring.auth.api.domain.model.permissao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;

//@Audited
@Entity
@Table(name = "permissao", schema = "auth")
public class Permissao extends BaseEntity {

    private static final long serialVersionUID = -93989467728114655L;

    @NotNull
    @Size(min = 3, max = 128)
    @Enumerated(EnumType.STRING)
    @Column(name = "papel", length = 128, nullable = false)
    private Papel papel;

    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "descricao", length = 128, nullable = false)
    private String descricao;

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
