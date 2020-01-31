package br.pro.fagnerlima.spring.auth.api.infrastructure.util;

import java.text.Normalizer;

public class StringUtils {

    public static String unaccent(String str) {
        String normalizedStr = Normalizer.normalize(str, Normalizer.Form.NFD);

        return normalizedStr.replaceAll("[^\\p{ASCII}]", "");
    }

}
