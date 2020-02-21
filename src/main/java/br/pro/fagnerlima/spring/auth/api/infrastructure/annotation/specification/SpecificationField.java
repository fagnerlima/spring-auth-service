package br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.specification;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.Operation;

@Retention(RUNTIME)
@Target({ FIELD })
public @interface SpecificationField {

    public String property() default "";

    public Operation operation() default Operation.EQUAL;

}
