package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Expression;

import org.springframework.data.jpa.domain.Specification;

import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationField;
import br.pro.fagnerlima.spring.auth.api.infrastructure.exception.SpecificationFilterException;
import br.pro.fagnerlima.spring.auth.api.infrastructure.util.FieldUtils;
import br.pro.fagnerlima.spring.auth.api.infrastructure.util.StringUtils;

public class SpecificationFactory<T> {

    public Specification<T> create(String property, Long value, Operation operation) {
        return (root, query, criteriaBuilder) -> {
            Expression<Long> x = root.get(property);
            Long y = value;

            switch (operation) {
                case GREATER_THAN:
                    return criteriaBuilder.greaterThan(x, y);
                case LESS_THAN:
                    return criteriaBuilder.lessThan(x, y);
                case GREATER_THAN_OR_EQUAL:
                    return criteriaBuilder.greaterThan(x, y);
                case LESS_THAN_OR_EQUAL:
                    return criteriaBuilder.lessThanOrEqualTo(x, y);
                default:
                    return criteriaBuilder.equal(x, y);
            }
        };
    }

    public Specification<T> create(String property, Integer value, Operation operation) {
        return create(property, Long.valueOf(value), operation);
    }

    public Specification<T> create(String property, Boolean value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(property), value);
    }

    public Specification<T> create(String property, Enum<?> value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(property), value);
    }

    public Specification<T> create(String property, LocalDate value, Operation operation) {
        return (root, query, criteriaBuilder) -> {
            Expression<LocalDate> x = root.get(property);
            LocalDate y = value;

            switch (operation) {
                case GREATER_THAN:
                    return criteriaBuilder.greaterThan(x, y);
                case LESS_THAN:
                    return criteriaBuilder.lessThan(x, y);
                case GREATER_THAN_OR_EQUAL:
                    return criteriaBuilder.greaterThanOrEqualTo(x, y);
                case LESS_THAN_OR_EQUAL:
                    return criteriaBuilder.lessThanOrEqualTo(x, y);
                default:
                    return criteriaBuilder.equal(x, y);
            }
        };
    }

    public Specification<T> create(String property, Collection<?> values) {
        return (root, query, criteriaBuilder) -> {
            In<Object> predicate = criteriaBuilder.in(root.get(property));
            values.stream().forEach(value -> predicate.value(value));

            return predicate;
        };
    }

    public Specification<T> create(String property, String value, Operation operation) {
        return (root, query, criteriaBuilder) -> {
            Expression<String> x;
            String y;

            switch (operation) {
                case EQUAL_IGNORE_CASE:
                    x = criteriaBuilder.lower(root.get(property));
                    y = value.toLowerCase();
                    return criteriaBuilder.equal(x, y);
                case LIKE:
                    x = root.get(property);
                    y = "%" + value + "%";
                    return criteriaBuilder.like(x, y);
                case LIKE_IGNORE_CASE:
                    x = criteriaBuilder.lower(root.get(property));
                    y = "%" + value.toLowerCase() + "%";
                    return criteriaBuilder.like(x, y);
                case EQUALS_IGNORE_CASE_UNACCENT:
                    x = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(root.get(property)));
                    y = br.pro.fagnerlima.spring.auth.api.infrastructure.util.StringUtils.unaccent(value.toLowerCase());
                    return criteriaBuilder.equal(x, y);
                case LIKE_IGNORE_CASE_UNACCENT:
                    x = criteriaBuilder.function("unaccent", String.class, criteriaBuilder.lower(root.get(property)));
                    y = "%" + br.pro.fagnerlima.spring.auth.api.infrastructure.util.StringUtils.unaccent(value.toLowerCase()) + "%";
                    return criteriaBuilder.like(x, y);
                default:
                    x = root.get(property);
                    y = value;
                    return criteriaBuilder.equal(x, y);
            }
        };
    }

    public Specification<T> create(Object filter) {
        try {
            SpecificationEntity specificationEntity = filter.getClass().getAnnotation(SpecificationEntity.class);
            List<Field> dataFields = FieldUtils.getAllFields(filter.getClass(), SpecificationField.class);
            List<Field> entityFields = FieldUtils.getAllFields(specificationEntity.value());
            List<Specification<T>> specs = new ArrayList<>();

            for (Field dataField : dataFields) {
                dataField.setAccessible(true);
                Object value = dataField.get(filter);

                if (value != null && hasProperty(entityFields, dataField)) {
                    specs.add(create(dataField, dataField.get(filter)));
                }
            }

            if (specs.isEmpty()) {
                return null;
            }

            Specification<T> result = specs.get(0);

            if (specs.size() > 1) {
                for (int i = 1; i < specs.size(); i++) {
                    result = Specification
                            .where(result)
                            .and(specs.get(i));
                }
            }

            return result;
        } catch (IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
            throw new SpecificationFilterException();
        }
    }

    private Boolean hasProperty(List<Field> entityFields, Field dataField) {
        return entityFields.stream().filter(ef -> {
                SpecificationField specificationField = dataField.getAnnotation(SpecificationField.class);

                if (specificationField == null) {
                    return false;
                }

                if (!StringUtils.isEmpty(specificationField.property())) {
                    return ef.getName().equals(specificationField.property());
                }

                return ef.getName().equals(dataField.getName());
            }).findFirst().isPresent();
    }

    private Specification<T> create(Field field, Object value) {
        if (!field.isAnnotationPresent(SpecificationField.class)) {
            return null;
        }

        SpecificationField specificationField = field.getAnnotation(SpecificationField.class);
        String property = StringUtils.isEmpty(specificationField.property()) ? field.getName() : specificationField.property();
        Operation operation = specificationField.operation();

        if (value instanceof Number) {
            return create(property, Long.valueOf(value.toString()), operation);
        }

        if (value instanceof Boolean) {
            return create(property, (Boolean) value);
        }

        if (value instanceof LocalDate) {
            return create(property, LocalDate.parse(value.toString()), operation);
        }

        if (value instanceof Enum<?>) {
            return create(property, (Enum<?>) value);
        }

        if (value instanceof Collection) {
            return create(property, (Collection<?>) value);
        }

        return create(property, value.toString(), operation);
    }

}
