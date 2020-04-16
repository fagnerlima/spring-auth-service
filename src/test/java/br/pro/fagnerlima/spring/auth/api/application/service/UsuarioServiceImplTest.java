package br.pro.fagnerlima.spring.auth.api.application.service;

import static br.pro.fagnerlima.spring.auth.api.test.util.ServiceTestUtils.assertAuditingFields;
import static br.pro.fagnerlima.spring.auth.api.test.util.ServiceTestUtils.assertPage;
import static br.pro.fagnerlima.spring.auth.api.test.util.ServiceTestUtils.assertPageNoContent;
import static br.pro.fagnerlima.spring.auth.api.test.util.ServiceTestUtils.createSpecification;
import static br.pro.fagnerlima.spring.auth.api.test.util.ServiceTestUtils.mockAuthenticationForAuditing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.BusinessException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.DuplicateKeyException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidActualPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidTokenException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.NotAuthenticatedUserException;
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.GrupoRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.UsuarioRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.BaseEntitySpecification;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.util.BcryptUtils;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MailService;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.email.MailRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioFilterRequestTO;
import br.pro.fagnerlima.spring.auth.api.test.builder.UsuarioBuilder;
import br.pro.fagnerlima.spring.auth.api.test.builder.UsuarioFilterRequestTOBuilder;
import br.pro.fagnerlima.spring.auth.api.test.util.UsuarioTestUtils;

@ActiveProfiles("test")
@SpringBootTest
public class UsuarioServiceImplTest {

    private static final String MOCK_LOGGED_ADMIN = Usuario.LOGIN_ADMIN;
    private static final String MOCK_SENHA_PREFIX = "Senha123#";
    private static final String MOCK_RESET_TOKEN_PREFIX = "Token123#";

    private UsuarioService usuarioService;

    private UsuarioRepository usuarioRepository;

    private GrupoRepository grupoRepository;

    @MockBean
    private MailService mailService;

    @Autowired
    public UsuarioServiceImplTest(UsuarioService usuarioService, UsuarioRepository usuarioRepository, GrupoRepository grupoRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.grupoRepository = grupoRepository;
    }

    @BeforeEach
    public void setUp() throws Exception {
        mockAuthenticationForAuditing(MOCK_LOGGED_ADMIN);
        doNothing().when(mailService).send(any(MailRequestTO.class));

        Set<Grupo> grupos = findGruposByIds(Grupo.ID_ADMIN);
        usuarioRepository.save(UsuarioTestUtils.createUsuarioPendente("José de Souza", "jose.souza", grupos));
        usuarioRepository.save(UsuarioTestUtils.createUsuarioPendente("José de Paula", "jose.paula", grupos));
        usuarioRepository.save(UsuarioTestUtils.createUsuarioInativo("Maria da Silva", "maria.silva", grupos));
        usuarioRepository.save(UsuarioTestUtils.createUsuarioBloqueado("Isabel dos Santos", "isabel.santos", grupos));
        usuarioRepository.save(UsuarioTestUtils.createUsuarioAtivo("Fagner Silva de Lima", "fagner.lima", grupos));
    }

    @AfterEach
    public void tearDown() throws Exception {
        Specification<Usuario> specification = BaseEntitySpecification.idGreaterThan(Usuario.ID_ADMIN);
        usuarioRepository.deleteAll(usuarioRepository.findAll(specification));
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
        Usuario usuario = usuarioService.findBySenhaResetToken(MOCK_RESET_TOKEN_PREFIX + "jose.souza");

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
    public void testFindAllByPageable_whenRoot() {
        mockAuthenticationForAuditing(Usuario.LOGIN_ROOT);
        Page<Usuario> usuariosPage = usuarioService.findAll(PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 8, 1, 8);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterById() {
        UsuarioFilterRequestTO filter = new UsuarioFilterRequestTOBuilder()
                .withId(Usuario.ID_ADMIN)
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(createSpecification(filter, Usuario.class), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 1, 1, 1);
        assertIsAdmin(usuariosPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByNome() {
        UsuarioFilterRequestTO filter = new UsuarioFilterRequestTOBuilder()
                .withNome("jose souza")
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(createSpecification(filter, Usuario.class), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 1, 1, 1);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByEmail() {
        UsuarioFilterRequestTO filter = new UsuarioFilterRequestTOBuilder()
                .withEmail("jose")
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(createSpecification(filter, Usuario.class), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByLogin() {
        UsuarioFilterRequestTO filter = new UsuarioFilterRequestTOBuilder()
                .withNome("jose")
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(createSpecification(filter, Usuario.class), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 2, 1, 2);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByAtivo() {
        UsuarioFilterRequestTO filter = new UsuarioFilterRequestTOBuilder()
                .withAtivo(true)
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(createSpecification(filter, Usuario.class), PageRequest.of(0, 10));

        assertPage(usuariosPage, 10, 0, 5, 1, 5);
    }

    @Test
    public void testFindAllBySpecificationAndPageable_notFound() {
        UsuarioFilterRequestTO filter = new UsuarioFilterRequestTOBuilder()
                .withNome("alberto")
                .build();

        Page<Usuario> usuariosPage = usuarioService.findAll(createSpecification(filter, Usuario.class), PageRequest.of(0, 10));

        assertPageNoContent(usuariosPage, 10, 0);
    }

    @Test
    public void findAllActive() {
        List<Usuario> usuarios = usuarioService.findAllActive();

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

        verifySendMail(1);
        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("miguel.lima@email.com");
        assertThat(usuario.getLogin()).isEqualTo("miguel.lima");
        assertThat(usuario.getPendente()).isTrue();
        assertThat(usuario.getBloqueado()).isFalse();
        assertThat(usuario.getSenha().getResetToken()).isNotBlank();
        assertThat(usuario.getSenha().getValor()).isNull();
        assertAuditingFields(usuario, MOCK_LOGGED_ADMIN);
    }

    @Test
    public void testSave_whenHasGrupoRoot() {
        Usuario usuario = UsuarioTestUtils.createUsuarioAtivo("Miguel Lima", "miguel.lima", findGruposByIds(Grupo.ID_ROOT));

        assertThrows(BusinessException.class, () -> usuarioService.save(usuario), "usuario.save.grupos.root");
    }

    @Test
    public void testSave_whenHasGrupoSystem() {
        Usuario usuario = UsuarioTestUtils.createUsuarioAtivo("Miguel Lima", "miguel.lima", findGruposByIds(Grupo.ID_SYSTEM));

        assertThrows(BusinessException.class, () -> usuarioService.save(usuario), "usuario.save.grupos.system");
    }

    @Test
    public void testSave_whenHasGrupoAdmin() {
        Usuario usuario = UsuarioTestUtils.createUsuarioAtivo("Miguel Lima", "miguel.lima", findGruposByIds(Grupo.ID_ADMIN));
        usuarioService.save(usuario);

        verifySendMail(1);
        assertThat(usuario.getId()).isNotNull();
    }

    @Test
    public void testSave_whenHasGrupoAdminAndAdminOrRootNotAuthenticated() {
        mockAuthenticationForAuditing("system");

        Usuario usuario = UsuarioTestUtils.createUsuarioAtivo("Miguel Lima", "miguel.lima", findGruposByIds(Grupo.ID_ADMIN));

        assertThrows(BusinessException.class, () -> usuarioService.save(usuario), "usuario.save.grupos.admin");
    }

    @Test
    public void testSave_whenEmailNotUnique() {
        Usuario usuario1 = UsuarioTestUtils.createUsuarioAtivo("Miguel Lima", "miguel.lima", findGruposByIds(Grupo.ID_ADMIN));
        usuarioService.save(usuario1);

        Usuario usuario2 = new UsuarioBuilder()
                .withNome("Miguel Lima")
                .withEmail("miguel.lima@email.com")
                .withLogin("miguel.lima1")
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .withAtivo(true)
                .build();

        assertThrows(DuplicateKeyException.class, () -> usuarioService.save(usuario2), "usuario.duplicate-key.email");
    }

    @Test
    public void testSave_whenLoginNotUnique() {
        Usuario usuario1 = UsuarioTestUtils.createUsuarioAtivo("Miguel Lima", "miguel.lima", findGruposByIds(Grupo.ID_ADMIN));
        usuarioService.save(usuario1);

        Usuario usuario2 = new UsuarioBuilder()
                .withNome("Miguel Lima")
                .withEmail("miguel.lima1@email.com")
                .withLogin("miguel.lima")
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .withAtivo(true)
                .build();

        assertThrows(DuplicateKeyException.class, () -> usuarioService.save(usuario2), "usuario.duplicate-key.login");
    }

    @Test
    public void testUpdate() {
        Usuario usuario = UsuarioTestUtils.createUsuarioAtivo("Miguel Lima", "miguel.lima", findGruposByIds(Grupo.ID_ADMIN));
        usuarioService.save(usuario);

        Usuario usuarioModificado = new UsuarioBuilder()
                .withNome("Miguel Borba Lima")
                .withEmail("miguel.lima@email.com")
                .withLogin("miguel.lima")
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .withAtivo(true)
                .build();
        Usuario usuarioAtualizado = usuarioService.update(usuario.getId(), usuarioModificado);

        verifySendMail(1);
        assertThat(usuarioAtualizado.getId()).isEqualTo(usuario.getId());
        assertThat(usuarioAtualizado.getNome()).isEqualTo("Miguel Borba Lima");
    }

    @Test
    public void testUpdate_whenRootGrupoAlterado() {
        Usuario usuario = UsuarioTestUtils.createUsuarioAtivo("Super User", "root", findGruposByIds(Grupo.ID_ADMIN));

        assertThrows(BusinessException.class, () -> usuarioService.update(Usuario.ID_ROOT, usuario), "usuario.update.grupos.root");
    }

    @Test
    public void testUpdate_whenSystemGrupoAlterado() {
        Usuario usuario = UsuarioTestUtils.createUsuarioAtivo("System User", "system", findGruposByIds(Grupo.ID_ADMIN));

        assertThrows(BusinessException.class, () -> usuarioService.update(Usuario.ID_SYSTEM, usuario), "usuario.update.grupos.system");
    }

    @Test
    public void testUpdate_whenAdminGrupoAlterado() {
        Usuario usuario = UsuarioTestUtils.createUsuarioAtivo("Super User", "root", findGruposByIds(Grupo.ID_SYSTEM));

        assertThrows(BusinessException.class, () -> usuarioService.update(Usuario.ID_ADMIN, usuario), "usuario.update.grupos.admin");
    }

    @Test
    public void testUpdateAutenticado() {
        Usuario usuario = usuarioRepository
                .save(UsuarioTestUtils.createUsuarioAtivo("Miguel Lima", "miguel.lima", findGruposByIds(Grupo.ID_ADMIN)));

        mockAuthenticationForAuditing("miguel.lima");

        Usuario usuarioAtualizado = usuarioService.updateAutenticado(new UsuarioBuilder()
                .withNome("Miguel Borba Lima")
                .withEmail("miguel.lima@email.com")
                .withLogin("miguel.lima")
                .withGrupos(findGruposByIds(Grupo.ID_ADMIN))
                .withAtivo(true)
                .build());

        assertThat(usuario.getId()).isEqualTo(usuarioAtualizado.getId());
        assertThat(usuarioAtualizado.getNome()).isEqualTo("Miguel Borba Lima");
    }

    @Test
    public void testUpdateSenhaByResetToken() {
        Usuario usuario = usuarioRepository
                .save(UsuarioTestUtils.createUsuarioPendente("Manoel Alves", "manoel.alves", findGruposByIds(Grupo.ID_ADMIN)));

        String valorSenha = MOCK_SENHA_PREFIX + "manoel.alves";
        Usuario usuarioAtualizado = usuarioService.updateSenhaByResetToken(usuario.getSenha().getResetToken(), valorSenha);

        assertThat(BcryptUtils.validate(valorSenha, usuarioAtualizado.getSenha().getValor())).isTrue();
        assertThat(usuarioAtualizado.getSenha().getResetToken()).isNull();
    }

    @Test
    public void testUpdateSenhaByResetToken_whenSenhaIsNotValid() {
        Usuario usuario = usuarioRepository
                .save(UsuarioTestUtils.createUsuarioPendente("Manoel Alves", "manoel.alves", findGruposByIds(Grupo.ID_ADMIN)));
        
        assertThrows(InvalidPasswordException.class,
                () -> usuarioService.updateSenhaByResetToken(usuario.getSenha().getResetToken(), "manoel.alves"));
    }

    @Test
    public void testUpdateSenhaAutenticado() {
        usuarioRepository.save(UsuarioTestUtils.createUsuarioAtivo("Alice Lima", "alice.lima", findGruposByIds(Grupo.ID_ADMIN)));
        mockAuthenticationForAuditing("alice.lima");

        Usuario usuarioAtualizado = usuarioService.updateSenhaAutenticado(MOCK_SENHA_PREFIX + "alice.lima", "Alice.Lima@987");

        assertThat(BcryptUtils.validate("Alice.Lima@987", usuarioAtualizado.getSenha().getValor()));
    }

    @Test
    public void testUpdateSenhaAutenticado_whenInvalidActualPassword() {
        usuarioRepository.save(UsuarioTestUtils.createUsuarioAtivo("Alice Lima", "alice.lima", findGruposByIds(Grupo.ID_ADMIN)));
        mockAuthenticationForAuditing("alice.lima");

        assertThrows(InvalidActualPasswordException.class,
                () -> usuarioService.updateSenhaAutenticado(MOCK_SENHA_PREFIX + "alicelima", "Alice.Lima@987"));
    }

    @Test
    public void testUpdateSenhaAutenticado_whenInvalidPassword() {
        usuarioRepository.save(UsuarioTestUtils.createUsuarioAtivo("Alice Lima", "alice.lima", findGruposByIds(Grupo.ID_ADMIN)));
        mockAuthenticationForAuditing("alice.lima");
        
        assertThrows(InvalidPasswordException.class,
                () -> usuarioService.updateSenhaAutenticado(MOCK_SENHA_PREFIX + "alice.lima", "alice.lima"));
    }

    @Test
    public void testRecoverLogin() {
        usuarioRepository.save(UsuarioTestUtils.createUsuarioAtivo("Alice Lima", "alice.lima", findGruposByIds(Grupo.ID_ADMIN)));

        usuarioService.recoverLogin("alice.lima@email.com");

        verifySendMail(1);
    }

    @Test
    public void testRecoverSenha() {
        usuarioRepository.save(UsuarioTestUtils.createUsuarioAtivo("Alice Lima", "alice.lima", findGruposByIds(Grupo.ID_ADMIN)));

        String email = "alice.lima@email.com";
        usuarioService.recoverSenha(email);
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).get();

        verifySendMail(1);
        assertThat(usuario.getSenha().getResetToken()).isNotNull();
    }

    private void assertIsAdmin(Usuario usuario) {
        assertThat(usuario.getId()).isEqualTo(Usuario.ID_ADMIN);
    }

    private void verifySendMail(int times) {
        verify(mailService, times(times)).send(any(MailRequestTO.class));
    }

    private Set<Grupo> findGruposByIds(Long... ids) {
        return new HashSet<>(grupoRepository.findAllById(List.of(ids)));
    }

}
