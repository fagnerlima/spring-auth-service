package br.pro.fagnerlima.spring.auth.api.domain.model.permissao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;

@Audited
@Entity
@Table(name = "permissao", schema = "auth")
public class Permissao extends BaseEntity<Integer> {

    private static final long serialVersionUID = -93989467728114655L;

    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "papel", length = 128, nullable = false)
    private String papel;

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    @Override
    public String toString() {
        return String.format("Permissao [id=%s, papel=%s]", id, papel);
    }
}
