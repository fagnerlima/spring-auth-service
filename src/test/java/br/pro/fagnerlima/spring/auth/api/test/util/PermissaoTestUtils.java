package br.pro.fagnerlima.spring.auth.api.test.util;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Papel;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.test.builder.PermissaoBuilder;

public class PermissaoTestUtils {

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

}
