package br.pro.fagnerlima.spring.auth.api.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Papel;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.permissao.PermissaoResponseTO;
import br.pro.fagnerlima.spring.auth.api.test.builder.PermissaoBuilder;

public class PermissaoTestUtils {

    public static void assertResponseTO(PermissaoResponseTO permissaoResponseTO, Permissao permissao) {
        assertThat(permissaoResponseTO.getId()).isEqualTo(permissao.getId());
        assertThat(permissaoResponseTO.getPapel()).isEqualTo(permissao.getPapel());
        assertThat(permissaoResponseTO.getDescricao()).isEqualTo(permissao.getDescricao());
        assertThat(permissaoResponseTO.getLinks()).isNullOrEmpty();
    }

    public static Permissao createPermissao(Papel papel, String descricao) {
        return new PermissaoBuilder()
                .withPapel(papel)
                .withDescricao(descricao)
                .build();
    }

    public static Permissao createPermissao(Long id, Papel papel, String descricao) {
        return new PermissaoBuilder()
                .withId(id)
                .withPapel(papel)
                .withDescricao(descricao)
                .build();
    }

    public static Permissao createPermissaoAdminMock() {
        return createPermissao(1L, Papel.ROLE_ADMIN, "Administrador");
    }

}
