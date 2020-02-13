package br.pro.fagnerlima.spring.auth.api.application.service;

import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.assertAuditingFields;
import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.assertPage;
import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.assertPageNoContent;
import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.mockAuthenticationForAuditing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MailService;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.email.MailRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioFilterRequestTO;
import br.pro.fagnerlima.spring.auth.api.testcase.builder.UsuarioBuilder;
import br.pro.fagnerlima.spring.auth.api.testcase.builder.UsuarioFilterRequestTOBuilder;

@ActiveProfiles("test")
@SpringBootTest
public class UsuarioServiceImplTest {

    private static final String MOCK_LOGGED_USERNAME = "admin";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private SpecificationFactory<Usuario> specificationFactory;

    @MockBean
    private MailService mailService;

    @BeforeEach
    public void setUp() throws Exception {
        mockAuthenticationForAuditing("admin");
        doNothing().when(mailService).send(any(MailRequestTO.class));

        usuarioRepository.save(new UsuarioBuilder()
                .withNome("José de Souza")
                .withEmail("jose.souza@email.com")
                .withLogin("jose.souza")
                .withAtivo(true)
                .withPendente(true)
                .withBloqueado(false)
                .withSenha(new Senha(null, "token#jose.souza"))
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .build());
        usuarioRepository.save(new UsuarioBuilder()
                .withNome("José de Paula")
                .withEmail("jose.paula@email.com")
                .withLogin("jose.paula")
                .withAtivo(true)
                .withPendente(true)
                .withBloqueado(false)
                .withSenha(new Senha(null, "token#jose.paula"))
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .build());
        usuarioRepository.save(new UsuarioBuilder()
                .withNome("Maria da Silva")
                .withEmail("maria.silva@email.com")
                .withLogin("maria.silva")
                .withAtivo(false)
                .withPendente(false)
                .withBloqueado(false)
                .withSenha(new Senha("senha#maria.silva", null))
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .build());
        usuarioRepository.save(new UsuarioBuilder()
                .withNome("Isabel Santos")
                .withEmail("isabel.santos@email.com")
                .withLogin("isabel.santos")
                .withAtivo(true)
                .withPendente(false)
                .withBloqueado(true)
                .withSenha(new Senha("senha#isabel.santos", null))
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .build());
        usuarioRepository.save(new UsuarioBuilder()
                .withNome("Fagner Lima")
                .withEmail("fagner.lima@email.com")
                .withLogin("fagner.lima")
                .withAtivo(true)
                .withPendente(false)
                .withBloqueado(false)
                .withSenha(new Senha("senha#fagner.lima", null))
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .build());
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
        assertThrows(InformationNotFoundException.class, () -> usuarioService.findById(999L));
    }

    @Test
    public void findByEmail() {
        Usuario usuario = usuarioService.findByEmail("jose.paula@email.com");

        assertThat(usuario.getNome()).isEqualTo("José de Paula");
    }

    @Test
    public void findByEmail_whenNotFound() {
        assertThrows(InformationNotFoundException.class, () -> usuarioService.findByEmail("test@email.com"));
    }

    @Test
    public void findBySenhaResetToken() {
        Usuario usuario = usuarioService.findBySenhaResetToken("token#jose.souza");

        assertThat(usuario.getNome()).isEqualTo("José de Souza");
    }

    @Test
    public void findBySenhaResetToken_whenNotFound() {
        assertThrows(InvalidTokenException.class, () -> usuarioService.findBySenhaResetToken("token?jose.souza"));
    }

    @Test
    public void testFindAllByPageable() {
        Page<Usuario> usuariosPage = usuarioService.findAll(PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 6, 1, 6);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterById() {
        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTOBuilder()
                .withId(Usuario.ID_ADMIN)
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 1, 1, 1);
        assertIsAdmin(usuariosPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByNome() {
        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTOBuilder()
                .withNome("jose")
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByEmail() {
        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTOBuilder()
                .withEmail("jose")
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByLogin() {
        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTOBuilder()
                .withNome("jose")
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByAtivo() {
        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTOBuilder()
                .withAtivo(true)
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 5, 1, 5);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_notFound() {
        UsuarioFilterRequestTO usuarioFilterRequestTO = new UsuarioFilterRequestTOBuilder()
                .withNome("alberto")
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(specificationFactory.create(usuarioFilterRequestTO), PageRequest.of(0, 10));

        assertPageNoContent(usuariosPage, 10, 0);
    }

    @Test
    public void findAllActives() {
        List<Usuario> usuarios = usuarioService.findAllActives();

        assertThat(usuarios).hasSize(2);
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

    @Test
    public void testSave() {
        Usuario usuario = new UsuarioBuilder()
                .withNome("Miguel Lima")
                .withEmail("miguel.LIMA@email.com")
                .withLogin("MIGUEL.lima")
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .withAtivo(true)
                .build();
        usuarioService.save(usuario);

        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("miguel.lima@email.com");
        assertThat(usuario.getLogin()).isEqualTo("miguel.lima");
        assertThat(usuario.getPendente()).isTrue();
        assertThat(usuario.getBloqueado()).isFalse();
        assertThat(usuario.getSenha().getResetToken()).isNotBlank();
        assertThat(usuario.getSenha().getValor()).isNull();
        assertAuditingFields(usuario, MOCK_LOGGED_USERNAME);
    }

    private void assertIsAdmin(Usuario usuario) {
        assertThat(usuario.getId()).isEqualTo(Usuario.ID_ADMIN);
        assertThat(usuario.getNome()).isEqualTo("Administrador");
        assertThat(usuario.getGrupos().stream()
                .anyMatch(Grupo::isAdmin)).isTrue();
    }

    private Set<Grupo> findGruposByIds(Long... ids) {
        return new HashSet<>(grupoRepository.findAllById(Arrays.asList(ids)));
    }

}
