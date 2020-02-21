package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification;

import org.springframework.data.jpa.domain.Specification;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario_;

public class UsuarioSpecification extends BaseEntitySpecification {

    public static Specification<Usuario> positiveIdAndActiveAndNotPendenteAndNotBloqueado() {
        return new SpecificationBuilder<Usuario>()
                .and(positiveIdAndActive())
                .and(Usuario_.PENDENTE, false)
                .and(Usuario_.BLOQUEADO, false)
                .build();
    }

}
