package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationField;
import br.pro.fagnerlima.spring.auth.api.infrastructure.util.FieldUtils;

public class SpecificationBuilder<T> {

    private List<Specification<T>> specs = new ArrayList<>();

    public Specification<T> build() {
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
    }

    public SpecificationBuilder<T> and(Specification<T> spec) {
        specs.add(spec);

        return this;
    }

    public SpecificationBuilder<T> and(String property, Long value, Operation operation) {
        and((root, query, criteriaBuilder) -> {
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
        });

        return this;
    }

    public SpecificationBuilder<T> and(String property, Integer value, Operation operation) {
        return and(property, Long.valueOf(value), operation);
    }

    public SpecificationBuilder<T> and(String property, Boolean value) {
        return and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(property), value));
    }

    public SpecificationBuilder<T> and(String property, Enum<?> value) {
        return and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(property), value));
    }

    public SpecificationBuilder<T> and(String property, LocalDate value, Operation operation) {
        and((root, query, criteriaBuilder) -> {
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
        });

        return this;
    }

    public SpecificationBuilder<T> and(String property, Collection<?> value) {
        return and((root, query, criteriaBuilder) -> {
            In<Object> predicate = criteriaBuilder.in(root.get(property));
            value.stream().forEach(v -> predicate.value(v));

            return predicate;
        });
    }

    public SpecificationBuilder<T> and(String property, String value, Operation operation) {
        and((root, query, criteriaBuilder) -> {
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
        });

        return this;
    }

    public SpecificationBuilder<T> and(Object data) {
        try {
            SpecificationEntity specificationEntity = data.getClass().getAnnotation(SpecificationEntity.class);
            List<Field> dataFields = FieldUtils.getAllFields(data.getClass(), SpecificationField.class);
            List<Field> entityFields = FieldUtils.getAllFields(specificationEntity.value());

            for (Field dataField : dataFields) {
                dataField.setAccessible(true);
                Object value = dataField.get(data);

                if (value != null && hasProperty(entityFields, dataField)) {
                    and(dataField, dataField.get(data));
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return this;
    }

    private Boolean hasProperty(List<Field> entityFields, Field dataField) {
        return entityFields.stream().filter(ef -> {
                SpecificationField specificationField = dataField.getAnnotation(SpecificationField.class);

                if (StringUtils.isNotEmpty(specificationField.property())) {
                    return ef.getName().equals(specificationField.property());
                }

                return ef.getName().equals(dataField.getName());
            }).findFirst().isPresent();
    }

    private SpecificationBuilder<T> and(Field field, Object value) {
        if (field.isAnnotationPresent(SpecificationField.class)) {
            SpecificationField specificationField = field.getAnnotation(SpecificationField.class);
            String property = StringUtils.isEmpty(specificationField.property()) ? field.getName() : specificationField.property();
            Operation operation = specificationField.operation();

            if (value instanceof Number) {
                and(property, Long.valueOf(value.toString()), operation);
            } else if (value instanceof Boolean) {
                and(property, (Boolean) value);
            } else if (value instanceof LocalDate) {
                and(property, LocalDate.parse(value.toString()), operation);
            } else if (value instanceof Enum<?>) {
                and(property, (Enum<?>) value);
            } else if (value instanceof Collection) {
                and(property, (Collection<?>) value);
            } else {
                and(property, value.toString(), operation);
            }
        }

        return this;
    }

}
