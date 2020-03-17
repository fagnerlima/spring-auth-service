package br.pro.fagnerlima.spring.auth.api.test.util;

import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.test.builder.GrupoBuilder;

public class GrupoTestUtils {

    public static Grupo createGrupo(String nome, Boolean ativo, Set<Permissao> permissoes) {
        return new GrupoBuilder()
                .withNome(nome)
                .withPermissoes(permissoes)
                .withAtivo(ativo)
                .build();
    }

    public static Grupo createGrupo(String nome, Boolean ativo) {
        return createGrupo(nome, ativo, null);
    }

}
