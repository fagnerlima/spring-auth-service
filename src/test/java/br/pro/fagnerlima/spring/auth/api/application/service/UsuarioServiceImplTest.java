package br.pro.fagnerlima.spring.auth.api.application.service;

import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.assertPage;
import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.assertPageNoContent;
import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.mockAuthenticationForAuditing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidTokenException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.NotAuthenticatedUserException;
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
    public void testFindById() {
        Usuario usuario = usuarioService.findById(Usuario.ID_ADMIN);

        assertIsAdmin(usuario);
    }

    @Test
    public void testFindById_whenNotFound() {
        assertThrows(InformationNotFoundException.class, () -> usuarioService.findById(99L));
    }

    @Test
    public void findByEmail() {
        createAndSaveUsuarioPendente("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioPendente("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        Usuario usuario = usuarioService.findByEmail("jose.paula@email.com");

        assertThat(usuario.getNome()).isEqualTo("José de Paula");
    }

    @Test
    public void findByEmail_whenNotFound() {
        createAndSaveUsuarioPendente("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioPendente("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        assertThrows(InformationNotFoundException.class, () -> usuarioService.findByEmail("test@email.com"));
    }

    @Test
    public void findBySenhaResetToken() {
        String resetToken = createAndSaveUsuarioPendente("José de Souza", "jose.souza@email.com", "jose.souza",
                Arrays.asList(Grupo.ID_ADMIN)).getSenha().getResetToken();
        createAndSaveUsuarioPendente("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        Usuario usuario = usuarioService.findBySenhaResetToken(resetToken);

        assertThat(usuario.getNome()).isEqualTo("José de Souza");
    }

    @Test
    public void findBySenhaResetToken_whenNotFound() {
        String resetToken = createAndSaveUsuarioPendente("José de Souza", "jose.souza@email.com", "jose.souza",
                Arrays.asList(Grupo.ID_ADMIN)).getSenha().getResetToken();
        createAndSaveUsuarioPendente("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        assertThrows(InvalidTokenException.class, () -> usuarioService.findBySenhaResetToken(resetToken + "modify"));
    }

    @Test
    public void testFindAllByPageable() {
        Page<Usuario> usuariosPage = usuarioService.findAll(PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 1, 1, 1);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterById() {
        createAndSaveUsuarioPendente("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioPendente("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setId(Usuario.ID_ADMIN);

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 1, 1, 1);
        assertIsAdmin(usuariosPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByNome() {
        createAndSaveUsuarioPendente("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioPendente("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setNome("jose");

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
        assertThat(usuariosPage.getContent().stream()
                .anyMatch(u -> !u.getNome().contains("José"))).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByEmail() {
        createAndSaveUsuarioPendente("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioPendente("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setEmail("jose");

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
        assertThat(usuariosPage.getContent().stream()
                .anyMatch(u -> !u.getEmail().contains("jose"))).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByLogin() {
        createAndSaveUsuarioPendente("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioPendente("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setEmail("jose");

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
        assertThat(usuariosPage.getContent().stream()
                .anyMatch(u -> !u.getEmail().contains("jose"))).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByAtivo() {
        createAndSaveUsuarioAtivo("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioInativo("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setAtivo(true);

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
        assertThat(usuariosPage.getContent().stream()
                .anyMatch(u -> !u.getAtivo())).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_notFound() {
        createAndSaveUsuarioAtivo("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioInativo("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));

        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTO();
        usuarioFilterRequestTO.setNome("ronaldo");

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPageNoContent(usuariosPage, 10, 0);
    }

    @Test
    public void findAllActives() {
        createAndSaveUsuarioAtivo("José de Souza", "jose.souza@email.com", "jose.souza", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioInativo("José de Paula", "jose.paula@email.com", "jose.paula", Arrays.asList(Grupo.ID_ADMIN));
        createAndSaveUsuarioAtivo("Antônio Santiago", "antonio.santiago@email.com", "antonio.santiago", Arrays.asList(Grupo.ID_ADMIN));

        List<Usuario> usuarios = usuarioService.findAllActives();

        assertThat(usuarios).hasSize(3);
    }

    @Test
    public void testGetAutenticado() {
        Usuario usuario = usuarioService.getAutenticado();

        assertIsAdmin(usuario);
    }

    @Test
    public void testGetAutenticado_whenNotAuthenticated() {
        mockAuthenticationForAuditing(null);

        assertThrows(NotAuthenticatedUserException.class, () -> usuarioService.getAutenticado());
    }

    private void assertIsAdmin(Usuario usuario) {
        assertThat(usuario.getId()).isEqualTo(Usuario.ID_ADMIN);
        assertThat(usuario.getNome()).isEqualTo("Administrador");
        assertThat(usuario.getGrupos().stream()
                .anyMatch(Grupo::isAdmin)).isTrue();
    }

    private Usuario createUsuarioPendente(String nome, String email, String login, List<Long> idsGrupos) {
        List<Grupo> grupos = grupoRepository.findAllById(idsGrupos);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setSenha(new Senha(null, RandomString.make(32)));
        usuario.setPendente(true);
        usuario.setBloqueado(false);
        usuario.setGrupos(new HashSet<>(grupos));

        return usuario;
    }

    private Usuario createAndSaveUsuarioPendente(String nome, String email, String login, List<Long> idsGrupos) {
        return usuarioRepository.save(createUsuarioPendente(nome, email, login, idsGrupos));
    }

    private Usuario createUsuarioAtivo(String nome, String email, String login, List<Long> idsGrupos) {
        List<Grupo> grupos = grupoRepository.findAllById(idsGrupos);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setSenha(new Senha(null, RandomString.make(32)));
        usuario.setPendente(false);
        usuario.setBloqueado(false);
        usuario.setGrupos(new HashSet<>(grupos));

        return usuario;
    }

    private Usuario createAndSaveUsuarioAtivo(String nome, String email, String login, List<Long> idsGrupos) {
        return usuarioRepository.save(createUsuarioAtivo(nome, email, login, idsGrupos));
    }

    private Usuario createUsuarioInativo(String nome, String email, String login, List<Long> idsGrupos) {
        List<Grupo> grupos = grupoRepository.findAllById(idsGrupos);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setSenha(new Senha(null, RandomString.make(32)));
        usuario.setPendente(false);
        usuario.setBloqueado(false);
        usuario.setGrupos(new HashSet<>(grupos));
        usuario.inativar();

        return usuario;
    }

    private Usuario createAndSaveUsuarioInativo(String nome, String email, String login, List<Long> idsGrupos) {
        return usuarioRepository.save(createUsuarioInativo(nome, email, login, idsGrupos));
    }

}
