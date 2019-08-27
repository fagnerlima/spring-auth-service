package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
                List<Field> dataFields = FieldUtils.getAllFields(data.getClass());
                List<Field> entityFields = FieldUtils.getAllFields(specificationEntity.value());

                for (Field field : dataFields) {
                    field.setAccessible(true);
                    Object value = field.get(data);

                    if (value != null && entityFields.stream().filter(ef -> ef.getName() == field.getName()).findFirst().isPresent()) {
                        addPredicate(field, field.get(data));
                    }
                }

                return this.criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            } catch (IllegalArgumentException | IllegalAccessException exception) {
                exception.printStackTrace();
            }

            return null;
        };
    }

    private void addPredicate(Field field, Object value) {
        if (field.isAnnotationPresent(SpecificationField.class)) {
            SpecificationField specificationField = field.getAnnotation(SpecificationField.class);
            String property = specificationField.property() == null || specificationField.property().isBlank()
                    ? field.getName() : specificationField.property();
            SpecificationOperation operation = specificationField.operation();
            Predicate predicate = null;

            if (value instanceof Number) {
                predicate = buildPredicate(property, Integer.parseInt(value.toString()), operation);
            } else if (value instanceof Boolean) {
                predicate = buildPredicate(property, (Boolean) value);
            } else if (value instanceof LocalDate) {
                predicate = buildPredicate(property, LocalDate.parse(value.toString()), operation);
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

    private Predicate buildPredicate(String property, String value, SpecificationOperation operation) {
        switch (operation) {
            case EQUAL:
                return criteriaBuilder.equal(root.get(property), value);
            case EQUAL_IGNORE_CASE:
                return criteriaBuilder.equal(criteriaBuilder.lower(root.get(property)), value.toLowerCase());
            case LIKE_IGNORE_CASE:
                return criteriaBuilder.like(criteriaBuilder.lower(root.get(property)), "%" + value.toLowerCase() + "%");
            default:
                return criteriaBuilder.equal(root.get(property), value);
        }
    }

}
