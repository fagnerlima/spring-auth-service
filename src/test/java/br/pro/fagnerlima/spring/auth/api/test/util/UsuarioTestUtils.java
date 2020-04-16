package br.pro.fagnerlima.spring.auth.api.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Senha;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.util.BcryptUtils;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioResponseTO;
import br.pro.fagnerlima.spring.auth.api.test.builder.UsuarioBuilder;

public class UsuarioTestUtils {

    private static final String MOCK_SENHA_PREFIX = "Senha123#";
    private static final String MOCK_RESET_TOKEN_PREFIX = "Token123#";

    public static void assertResponseTO(UsuarioResponseTO usuarioResponseTO, Usuario usuario) {
        assertThat(usuarioResponseTO.getId()).isEqualTo(usuario.getId());
        assertThat(usuarioResponseTO.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioResponseTO.getEmail()).isEqualTo(usuario.getEmail());
        assertThat(usuarioResponseTO.getLogin()).isEqualTo(usuario.getLogin());
        assertThat(usuarioResponseTO.getPendente()).isEqualTo(usuario.getPendente());
        assertThat(usuarioResponseTO.getBloqueado()).isEqualTo(usuario.getBloqueado());
        assertThat(usuarioResponseTO.getAtivo()).isEqualTo(usuario.getAtivo());
        assertThat(usuarioResponseTO.getLinks()).hasSize(3);

        usuarioResponseTO.getGrupos().stream()
                .forEach(grupoResponseTO -> {
                    Optional<Grupo> grupoOpt = usuario.getGrupos().stream()
                            .filter(permissao -> permissao.getId().equals(grupoResponseTO.getId()))
                            .findFirst();
                    GrupoTestUtils.assertResponseTO(grupoResponseTO, grupoOpt.get());
                });
    }

    public static Usuario createUsuarioPendente(String nome, String login, Set<Grupo> grupos) {
        return createUsuario(nome, login, true, true, false, grupos);
    }

    public static Usuario createUsuarioAtivo(String nome, String login, Set<Grupo> grupos) {
        return createUsuario(nome, login, true, false, false, grupos);
    }

    public static Usuario createUsuarioAtivo(Long id, String nome, String login, Set<Grupo> grupos) {
        return createUsuario(id, nome, login, true, false, false, grupos);
    }

    public static Usuario createUsuarioInativo(String nome, String login, Set<Grupo> grupos) {
        return createUsuario(nome, login, false, false, false, grupos);
    }

    public static Usuario createUsuarioBloqueado(String nome, String login, Set<Grupo> grupos) {
        return createUsuario(nome, login, true, false, true, grupos);
    }

    public static Usuario createUsuario(Long id, String nome, String login, Boolean ativo, Boolean pendente, Boolean bloqueado,
            Set<Grupo> grupos) {
        return new UsuarioBuilder()
                .withId(id)
                .withNome(nome)
                .withEmail(login + "@email.com")
                .withLogin(login)
                .withAtivo(ativo)
                .withPendente(pendente)
                .withBloqueado(bloqueado)
                .withSenha(new Senha(
                        !pendente && !bloqueado ? BcryptUtils.encode(MOCK_SENHA_PREFIX + login) : null,
                        pendente || bloqueado ? MOCK_RESET_TOKEN_PREFIX + login : null))
                .withGrupos(grupos)
                .build();
    }

    public static Usuario createUsuario(Long id, String nome, String login, Boolean ativo, Set<Grupo> grupos) {
        return createUsuario(id, nome, login, ativo, false, false, grupos);
    }

    public static Usuario createUsuario(String nome, String login, Boolean ativo, Boolean pendente, Boolean bloqueado, Set<Grupo> grupos) {
        return createUsuario(null, nome, login, ativo, pendente, bloqueado, grupos);
    }

    public static Usuario createUsuarioAdminMock() {
        return createUsuario(1L, "Administrador", "admin", true, false, false, Set.of(GrupoTestUtils.createGrupoAdminMock()));
    }

}
