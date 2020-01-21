package br.pro.fagnerlima.spring.auth.api.infrastructure.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FieldUtils {

    @SuppressWarnings("unchecked")
    public static Collection<Long> getLongValues(Object data, Field field) throws IllegalAccessException {
        return (Collection<Long>) field.get(data);
    }

    /**
     * Retorna todos os campos da classe {type}, incluindo os campos de suas superclasses.
     *
     * @param type classe alvo
     * @return todos os campos da classe {type}
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();

        for (Class<?> t = type; t != null; t = t.getSuperclass()) {
            fields.addAll(Arrays.asList(t.getDeclaredFields()));
        }

        return fields;
    }

    /**
     * Retorna todos os campos da classe {type} anotados com {annotation}, incluindo os campos de suas superclasses.
     *
     * @param type classe alvo
     * @param annotationField annotation dos campos
     * @return todos os campos da classe {type} com a annotation {annotation}
     */
    public static <T extends Annotation> List<Field> getAllFields(Class<?> type, Class<T> annotationField) {
        List<Field> fields = getAllFields(type);

        return fields.stream()
                .filter(f -> f.getAnnotation(annotationField) != null)
                .collect(Collectors.toList());
    }

    /**
     * Retorna o campo de nome {name} da classe {type}
     *
     * @param type classe alvo
     * @param name nome do campo
     * @return o campo de nome {name}
     * @throws NoSuchFieldException
     */
    public static Field getField(Class<?> type, String name) throws NoSuchFieldException {
        List<Field> fields = getAllFields(type);

        return fields.stream().filter(field -> field.getName().equals(name)).findFirst()
                .orElseThrow(() -> new NoSuchFieldException(name));
    }

}
