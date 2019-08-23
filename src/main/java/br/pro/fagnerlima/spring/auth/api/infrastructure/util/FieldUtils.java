package br.pro.fagnerlima.spring.auth.api.infrastructure.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class FieldUtils {

    @SuppressWarnings("unchecked")
    public static Collection<Long> getLongValues(Object data, Field field) throws IllegalAccessException {
        return (Collection<Long>) field.get(data);
    }

    public static Class<?> getGenericType(Field field) {
        ParameterizedType fieldType = (ParameterizedType) field.getGenericType();

        return (Class<?>) fieldType.getActualTypeArguments()[0];
    }

}
