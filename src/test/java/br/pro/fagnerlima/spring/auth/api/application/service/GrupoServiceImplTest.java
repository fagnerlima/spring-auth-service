package br.pro.fagnerlima.spring.auth.api.application.service;

import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.assertAuditingFields;
import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.assertPage;
import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.assertPageNoContent;
import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.createSpecification;
import static br.pro.fagnerlima.spring.auth.api.testcase.ServiceTestCase.mockAuthenticationForAuditing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.GrupoRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.PermissaoRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.BaseEntitySpecification;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoFilterRequestTO;
import br.pro.fagnerlima.spring.auth.api.testcase.builder.GrupoBuilder;
import br.pro.fagnerlima.spring.auth.api.testcase.builder.GrupoFilterRequestTOBuilder;

@ActiveProfiles("test")
@SpringBootTest
public class GrupoServiceImplTest {

    private static final String MOCK_LOGGED_ADMIN = Usuario.LOGIN_ADMIN;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @BeforeEach
    public void setUp() throws Exception {
        mockAuthenticationForAuditing(MOCK_LOGGED_ADMIN);

        grupoRepository.save(createGrupo("Gerência", true, 1L));
        grupoRepository.save(createGrupo("Recepção", true, 2L, 3L, 4L));
        grupoRepository.save(createGrupo("Recursos Humanos", true, 2L, 3L, 4L, 5L));
        grupoRepository.save(createGrupo("Vendas", false));
    }

    @AfterEach
    public void tearDown() throws Exception {
        Specification<Grupo> specification = BaseEntitySpecification.idGreaterThan(Grupo.ID_ADMIN);
        grupoRepository.findAll(specification).stream()
                .forEach(g -> grupoRepository.delete(g));
    }

    @Test
    public void testFindById() {
        Grupo grupo = grupoService.findById(Grupo.ID_ADMIN);
        assertIsAdmin(grupo);
    }

    @Test
    public void testFindById_whenNotFound() {
        assertThrows(InformationNotFoundException.class, () -> grupoService.findById(99L));
    }

    @Test
    public void testFindAllByPageable() {
        Page<Grupo> gruposPage = grupoService.findAll(PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 5, 1, 5);
        assertIsAdmin(gruposPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterById() {
        GrupoFilterRequestTO filter = new GrupoFilterRequestTOBuilder()
                .withId(Grupo.ID_ADMIN)
                .build();

        Page<Grupo> gruposPage = grupoService.findAll(createSpecification(filter), PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 1, 1, 1);
        assertIsAdmin(gruposPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByNome() {
        GrupoFilterRequestTO filter = new GrupoFilterRequestTOBuilder()
                .withNome("recepcao")
                .build();

        Page<Grupo> gruposPage = grupoService.findAll(createSpecification(filter), PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 1, 1, 1);
        assertThat(gruposPage.getContent().get(0).getNome()).isEqualTo("Recepção");
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByAtivo() {
        GrupoFilterRequestTO filter = new GrupoFilterRequestTOBuilder()
                .withAtivo(true)
                .build();

        Page<Grupo> gruposPage = grupoService.findAll(createSpecification(filter), PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 4, 1, 4);
        assertThat(gruposPage.getContent().stream()
                .anyMatch(g -> !g.getAtivo())).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_notFound() {
        GrupoFilterRequestTO filter = new GrupoFilterRequestTOBuilder()
                .withNome("tecnologia da informacao")
                .build();

        Page<Grupo> gruposPage = grupoService.findAll(createSpecification(filter), PageRequest.of(0, 10));

        assertPageNoContent(gruposPage, 10, 0);
    }

    @Test
    public void testFindAllActive() {
        List<Grupo> grupos = grupoService.findAllActive();

        assertThat(grupos).hasSize(4);
    }

    @Test
    public void testSave() {
        Grupo grupo = grupoService.save(new GrupoBuilder()
                .withNome("Marketing")
                .withPermissoes(new HashSet<>(permissaoRepository.findAllById(Arrays.asList(4L, 5L, 6L))))
                .build());

        assertThat(grupo.getId()).isGreaterThan(1L);
        assertThat(grupo.getNome()).isEqualTo("Marketing");
        assertThat(grupo.getPermissoes()).hasSize(3);
        assertThat(grupo.getAtivo()).isTrue();
        assertAuditingFields(grupo, MOCK_LOGGED_ADMIN);
    }

    @Test
    public void testUpdate() {
        Long idGrupo = grupoRepository.save(createGrupo("Marketing", false)).getId();

        Grupo grupo = grupoService.update(idGrupo, new GrupoBuilder()
                .withNome("Marketing")
                .withPermissoes(new HashSet<>(permissaoRepository.findAllById(Arrays.asList(4L, 5L, 6L))))
                .withAtivo(true)
                .build());

        assertThat(grupo.getId()).isEqualTo(idGrupo);
        assertThat(grupo.getNome()).isEqualTo("Marketing");
        assertThat(grupo.getPermissoes()).hasSize(3);
        assertThat(grupo.getAtivo()).isTrue();
        assertAuditingFields(grupo, MOCK_LOGGED_ADMIN);
    }

    @Test
    public void testUpdate_whenNotFound() {
        Grupo grupo = createGrupo("Marketing", true, 4L);

        assertThrows(InformationNotFoundException.class, () -> grupoService.update(99L, grupo));
    }

    @Test
    public void testSwitchActive() {
        Long idGrupo = grupoRepository.save(createGrupo("Marketing", false, 4L, 5L, 6L)).getId();

        Grupo grupo = grupoService.switchActive(idGrupo);

        assertThat(grupo.getId()).isEqualTo(idGrupo);
        assertThat(grupo.getNome()).isEqualTo("Marketing");
        assertThat(grupo.getPermissoes()).hasSize(3);
        assertThat(grupo.getAtivo()).isTrue();
        assertAuditingFields(grupo, MOCK_LOGGED_ADMIN);
    }

    @Test
    public void testSwitchActive_whenNotFound() {
        assertThrows(InformationNotFoundException.class, () -> grupoService.switchActive(99L));
    }

    private void assertIsAdmin(Grupo grupo) {
        assertThat(grupo.getId()).isEqualTo(Grupo.ID_ADMIN);
        assertThat(grupo.getPermissoes().stream()
                .anyMatch(Permissao::hasAdmin)).isTrue();
    }

    private Grupo createGrupo(String nome, Boolean ativo, Long... idsPermissoes) {
        return new GrupoBuilder()
                .withNome(nome)
                .withAtivo(ativo)
                .withPermissoes(idsPermissoes != null && idsPermissoes.length > 0
                        ? new HashSet<>(permissaoRepository.findAllById(Arrays.asList(idsPermissoes)))
                        : null)
                .build();
    }

}
