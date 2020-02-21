package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification;

import org.springframework.data.jpa.domain.Specification;

import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity_;

public class BaseEntitySpecification {

    public static <T extends BaseEntity> Specification<T> idGreaterThan(Long id) {
        return new SpecificationBuilder<T>()
                .and(BaseEntity_.ID, id, Operation.GREATER_THAN)
                .build();
    }

    public static <T extends BaseEntity> Specification<T> positiveId() {
        return idGreaterThan(0L);
    }

    public static <T extends BaseEntity> Specification<T> active() {
        return new SpecificationBuilder<T>()
                .and(BaseEntity_.ATIVO, true)
                .build();
    }

    public static <T extends BaseEntity> Specification<T> positiveIdAndActive() {
        return new SpecificationBuilder<T>()
                .and(positiveId())
                .and(active())
                .build();
    }

}
