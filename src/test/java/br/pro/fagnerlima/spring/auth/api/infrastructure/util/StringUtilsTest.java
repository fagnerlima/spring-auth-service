package br.pro.fagnerlima.spring.auth.api.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void testUnaccent() {
        assertThat(StringUtils.unaccent("Teste")).isEqualTo("Teste");
        assertThat(StringUtils.unaccent("Salão de Recepção")).isEqualTo("Salao de Recepcao");
        assertThat(StringUtils.unaccent("@#$%")).isEqualTo("@#$%");
    }

    @Test
    public void testIsEmpty() {
        assertThat(StringUtils.isEmpty("")).isTrue();
        assertThat(StringUtils.isEmpty(null)).isTrue();
        assertThat(StringUtils.isEmpty("  ")).isFalse();
        assertThat(StringUtils.isEmpty(" a ")).isFalse();
    }

    @Test
    public void testIsBlank() {
        assertThat(StringUtils.isBlank("")).isTrue();
        assertThat(StringUtils.isBlank(null)).isTrue();
        assertThat(StringUtils.isBlank("  ")).isTrue();
        assertThat(StringUtils.isBlank(" a ")).isFalse();
    }

}
