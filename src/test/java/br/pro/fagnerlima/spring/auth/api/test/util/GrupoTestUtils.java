package br.pro.fagnerlima.spring.auth.api.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoResponseTO;
import br.pro.fagnerlima.spring.auth.api.test.builder.GrupoBuilder;

public class GrupoTestUtils {

    public static void assertResponseTO(GrupoResponseTO grupoResponseTO, Grupo grupo) {
        assertThat(grupoResponseTO.getId()).isEqualTo(grupo.getId());
        assertThat(grupoResponseTO.getNome()).isEqualTo(grupo.getNome());
        assertThat(grupoResponseTO.getAtivo()).isEqualTo(grupo.getAtivo());
        assertThat(grupoResponseTO.getLinks()).hasSize(3);

        grupoResponseTO.getPermissoes().stream()
                .forEach(permissaoResponseTO -> {
                    Optional<Permissao> permissaoOpt = grupo.getPermissoes().stream()
                            .filter(permissao -> permissao.getId().equals(permissaoResponseTO.getId()))
                            .findFirst();
                    PermissaoTestUtils.assertResponseTO(permissaoResponseTO, permissaoOpt.get());
                });
    }

    public static Grupo createGrupo(String nome, Boolean ativo, Set<Permissao> permissoes) {
        return new GrupoBuilder()
                .withNome(nome)
                .withPermissoes(permissoes)
                .withAtivo(ativo)
                .build();
    }

    public static Grupo createGrupo(Long id, String nome, Boolean ativo, Set<Permissao> permissoes) {
        return new GrupoBuilder()
                .withId(id)
                .withNome(nome)
                .withPermissoes(permissoes)
                .withAtivo(ativo)
                .build();
    }

    public static Grupo createGrupo(String nome, Boolean ativo) {
        return createGrupo(nome, ativo, null);
    }

    public static Grupo createGrupo(Long id, String nome, Boolean ativo) {
        return createGrupo(id, nome, ativo, null);
    }

    public static Grupo createGrupoAdminMock() {
        return createGrupo(1L, "Administrador", true, Set.of(PermissaoTestUtils.createPermissaoAdminMock()));
    }

}
