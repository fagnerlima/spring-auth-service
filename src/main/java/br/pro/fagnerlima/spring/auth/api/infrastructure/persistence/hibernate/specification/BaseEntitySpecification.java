package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification;

import org.springframework.data.jpa.domain.Specification;

import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity_;

public class BaseEntitySpecification {

    public static <T extends BaseEntity> Specification<T> idGreaterThan(Long id) {
        return new SpecificationFactory<T>().create(BaseEntity_.ID, id, Operation.GREATER_THAN);
    }

    public static <T extends BaseEntity> Specification<T> positiveId() {
        return idGreaterThan(0L);
    }

    public static <T extends BaseEntity> Specification<T> active() {
        return new SpecificationFactory<T>().create(BaseEntity_.ATIVO, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseEntity> Specification<T> positiveIdAndActive() {
        return (Specification<T>) Specification
                .where(positiveId())
                .and(active());
    }

}
