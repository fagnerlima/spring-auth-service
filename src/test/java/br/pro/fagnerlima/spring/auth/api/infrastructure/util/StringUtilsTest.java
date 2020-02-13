package br.pro.fagnerlima.spring.auth.api.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import br.pro.fagnerlima.spring.auth.api.infrastructure.util.StringUtils;

public class StringUtilsTest {

    @Test
    public void testUnaccent() {
        assertThat(StringUtils.unaccent("Teste")).isEqualTo("Teste");
        assertThat(StringUtils.unaccent("Salão de Recepção")).isEqualTo("Salao de Recepcao");
        assertThat(StringUtils.unaccent("@#$%")).isEqualTo("@#$%");
    }

}
