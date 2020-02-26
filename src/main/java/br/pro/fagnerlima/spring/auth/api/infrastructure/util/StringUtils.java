package br.pro.fagnerlima.spring.auth.api.infrastructure.util;

import java.text.Normalizer;

public class StringUtils {

    /**
     * Remove os acentos de uma string.
     * @param str string a ser manipulada
     * @return string sem acentos
     */
    public static String unaccent(String str) {
        String normalizedStr = Normalizer.normalize(str, Normalizer.Form.NFD);

        return normalizedStr.replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Verifica se a string é vazia ou nula.
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("nome")     = false
     * StringUtils.isEmpty("  nome  ") = false
     * </pre>
     *
     * @param str string a ser verificada
     * @return {@code true} se a string for vazia ou nula
     */
    public static Boolean isEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isEmpty(str);
    }

}
