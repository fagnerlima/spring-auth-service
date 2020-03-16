package br.pro.fagnerlima.spring.auth.api.infrastructure.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FieldUtils {

    @SuppressWarnings("unchecked")
    public static Collection<Long> getLongValues(Object data, Field field) throws IllegalAccessException {
        return (Collection<Long>) field.get(data);
    }

    /**
     * Retorna todos os campos da classe {@code type}, incluindo os campos de suas superclasses.
     *
     * @param type classe alvo
     * @return todos os campos da classe {@code type}
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        for (Class<?> t = type; t != null; t = t.getSuperclass()) {
            fields.addAll(List.of(t.getDeclaredFields()));
        }

        return fields;
    }

    /**
     * Retorna o campo de nome {@code name} da classe {@code type}
     *
     * @param type classe alvo
     * @param name nome do campo
     * @return o campo de nome {@code name}
     * @throws NoSuchFieldException
     */
    public static Field getField(Class<?> type, String name) throws NoSuchFieldException {
        List<Field> fields = getAllFields(type);

        return fields.stream().filter(field -> field.getName().equals(name)).findFirst()
                .orElseThrow(() -> new NoSuchFieldException(name));
    }

    public static Method findGetterMethod(String propertyName, Class<?> type) throws IntrospectionException {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, type);

        return propertyDescriptor.getReadMethod();
    }

    public static Method findSetterMethod(String propertyName, Class<?> type) throws IntrospectionException {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, type);

        return propertyDescriptor.getWriteMethod();
    }

}
