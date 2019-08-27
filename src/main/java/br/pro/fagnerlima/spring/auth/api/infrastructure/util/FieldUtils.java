package br.pro.fagnerlima.spring.auth.api.infrastructure.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FieldUtils {

    @SuppressWarnings("unchecked")
    public static Collection<Long> getLongValues(Object data, Field field) throws IllegalAccessException {
        return (Collection<Long>) field.get(data);
    }

    public static Class<?> getGenericType(Field field) {
        ParameterizedType fieldType = (ParameterizedType) field.getGenericType();

        return (Class<?>) fieldType.getActualTypeArguments()[0];
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        for (Class<?> t = type; t != null; t = t.getSuperclass()) {
            fields.addAll(Arrays.asList(t.getDeclaredFields()));
        }

        return fields;
    }

}
