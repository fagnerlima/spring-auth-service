package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification.SpecificationField;
import br.pro.fagnerlima.spring.auth.api.infrastructure.util.FieldUtils;

@Component
public class SpecificationFactory<T> {

    private CriteriaBuilder criteriaBuilder;

    private Root<T> root;

    private List<Predicate> predicates;

    public Specification<T> create(Object data) {
        return (root, query, criteriaBuilder) -> {
            try {
                this.criteriaBuilder = criteriaBuilder;
                this.root = root;
                predicates = new ArrayList<>();

                SpecificationEntity specificationEntity = data.getClass().getAnnotation(SpecificationEntity.class);
                List<Field> dataFields = FieldUtils.getAllFields(data.getClass(), SpecificationField.class);
                List<Field> entityFields = FieldUtils.getAllFields(specificationEntity.value());

                for (Field dataField : dataFields) {
                    dataField.setAccessible(true);
                    Object value = dataField.get(data);

                    if (value != null && hasProperty(entityFields, dataField)) {
                        addPredicate(dataField, dataField.get(data));
                    }
                }

                return this.criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            } catch (IllegalArgumentException | IllegalAccessException exception) {
                exception.printStackTrace();
            }

            return null;
        };
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

    private void addPredicate(Field field, Object value) {
        if (field.isAnnotationPresent(SpecificationField.class)) {
            SpecificationField specificationField = field.getAnnotation(SpecificationField.class);
            String property = StringUtils.isEmpty(specificationField.property()) ? field.getName() : specificationField.property();
            SpecificationOperation operation = specificationField.operation();
            Predicate predicate = null;

            if (value instanceof Number) {
                predicate = buildPredicate(property, Integer.parseInt(value.toString()), operation);
            } else if (value instanceof Boolean) {
                predicate = buildPredicate(property, (Boolean) value);
            } else if (value instanceof LocalDate) {
                predicate = buildPredicate(property, LocalDate.parse(value.toString()), operation);
            } else if (value instanceof Enum<?>) {
                predicate = buildPredicate(property, (Enum<?>) value);
            } else if (value instanceof Collection) {
                predicate = buildPredicate(property, (Collection<?>) value);
            } else {
                predicate = buildPredicate(property, value.toString(), operation);
            }

            predicates.add(predicate);
        }
    }

    private Predicate buildPredicate(String property, Integer value, SpecificationOperation operation) {
        switch (operation) {
            case EQUAL:
                return criteriaBuilder.equal(root.get(property), value);
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(property), value);
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(property), value);
            case GREATER_THAN_OR_EQUAL:
                return criteriaBuilder.greaterThan(root.get(property), value);
            case LESS_THAN_OR_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(root.get(property), value);
            default:
                return criteriaBuilder.equal(root.get(property), value);
        }
    }

    private Predicate buildPredicate(String property, Boolean value) {
        return criteriaBuilder.equal(root.get(property), value);
    }

    private Predicate buildPredicate(String property, Enum<?> value) {
        return criteriaBuilder.equal(root.get(property), value);
    }

    private Predicate buildPredicate(String property, LocalDate value, SpecificationOperation operation) {
        switch (operation) {
            case EQUAL:
                return criteriaBuilder.equal(root.get(property), value);
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(property), value);
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(property), value);
            case GREATER_THAN_OR_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(property), value);
            case LESS_THAN_OR_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(root.get(property), value);
            default:
                return criteriaBuilder.equal(root.get(property), value);
        }
    }

    private Predicate buildPredicate(String property, Collection<?> value) {
        In<Object> in = criteriaBuilder.in(root.get(property));
        value.stream().forEach(v -> in.value(v));

        return in;
    }

    private Predicate buildPredicate(String property, String value, SpecificationOperation operation) {
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
    }

}
