package br.pro.fagnerlima.spring.auth.api.application.service;

import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.assertPage;
import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.mockAuthenticationForAuditing;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Senha;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.GrupoRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.UsuarioRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioFilterRequestTO;

@ActiveProfiles("test")
@SpringBootTest
public class UsuarioServiceImplTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private SpecificationFactory<Usuario> specificationFactory;

    @BeforeEach
    public void setUp() throws Exception {
        mockAuthenticationForAuditing("admin");
    }

    @AfterEach
    public void tearDown() throws Exception {
        usuarioRepository.findAll().stream()
                .filter(u -> !u.isAdmin())
                .forEach(u -> usuarioRepository.delete(u));
    }

    @Test
    public void testFindByAdministrador() {
        Usuario usuario = usuarioService.findById(Usuario.ID_ADMIN);

        assertIsAdmin(usuario);
    }

    @Test
    public void testFindAllByPageable() {
        Page<Usuario> usuariosPage = usuarioService.findAll(PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 1, 1, 1);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterById() {
        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setId(Usuario.ID_ADMIN);

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 1, 1, 1);
        assertIsAdmin(usuariosPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByNome() {
        List<Grupo> grupos = grupoRepository.findAllById(Arrays.asList(Grupo.ID_ADMIN));
        usuarioRepository.save(new Usuario("José de Souza", "jose.souza@email.com", "jose.souza", new Senha(null, "token123"), true, false,
                new HashSet<>(grupos)));
        usuarioRepository.save(new Usuario("José de Paula", "jose.paula@email.com", "jose.paula", new Senha(null, "token456"), true, false,
                new HashSet<>(grupos)));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setNome("jose");

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
        assertThat(usuariosPage.getContent().stream()
                .anyMatch(u -> !u.getNome().contains("José"))).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByEmail() {
        List<Grupo> grupos = grupoRepository.findAllById(Arrays.asList(Grupo.ID_ADMIN));
        usuarioRepository.save(new Usuario("José de Souza", "jose.souza@email.com", "jose.souza", new Senha(null, "token123"), true, false,
                new HashSet<>(grupos)));
        usuarioRepository.save(new Usuario("José de Paula", "jose.paula@email.com", "jose.paula", new Senha(null, "token456"), true, false,
                new HashSet<>(grupos)));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setEmail("jose");

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
        assertThat(usuariosPage.getContent().stream()
                .anyMatch(u -> !u.getEmail().contains("jose"))).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByLogin() {
        List<Grupo> grupos = grupoRepository.findAllById(Arrays.asList(Grupo.ID_ADMIN));
        usuarioRepository.save(new Usuario("José de Souza", "jose.souza@email.com", "jose.souza", new Senha(null, "token123"), true, false,
                new HashSet<>(grupos)));
        usuarioRepository.save(new Usuario("José de Paula", "jose.paula@email.com", "jose.paula", new Senha(null, "token456"), true, false,
                new HashSet<>(grupos)));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setEmail("jose");

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
        assertThat(usuariosPage.getContent().stream()
                .anyMatch(u -> !u.getEmail().contains("jose"))).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByAtivo() {
        List<Grupo> grupos = grupoRepository.findAllById(Arrays.asList(Grupo.ID_ADMIN));
        usuarioRepository.save(new Usuario("José de Souza", "jose.souza@email.com", "jose.souza", new Senha(null, "token123"), true, false,
                new HashSet<>(grupos)));

        Usuario usuarioInativo = new Usuario("José de Paula", "jose.paula@email.com", "jose.paula", new Senha(null, "token456"), true, false,
                new HashSet<>(grupos));
        usuarioInativo.setAtivo(false);
        usuarioRepository.save(usuarioInativo);

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setAtivo(true);

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
        assertThat(usuariosPage.getContent().stream()
                .anyMatch(u -> !u.getAtivo())).isFalse();
    }

    private void assertIsAdmin(Usuario usuario) {
        assertThat(usuario.getId()).isEqualTo(Usuario.ID_ADMIN);
        assertThat(usuario.getNome()).isEqualTo("Administrador");
        assertThat(usuario.getGrupos().stream()
                .anyMatch(Grupo::isAdmin)).isTrue();
    }

}
