package br.pro.fagnerlima.spring.auth.api.application.service;

import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.assertAuditingFields;
import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.assertPage;
import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.assertPageNoContent;
import static br.pro.fagnerlima.spring.auth.api.test.ServiceTestUtils.mockAuthenticationForAuditing;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.GrupoRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.PermissaoRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoFilterRequestTO;

@ActiveProfiles("test")
@SpringBootTest
public class GrupoServiceImplTest {

    private static final String MOCK_LOGGED_USERNAME = "admin";

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Autowired
    private SpecificationFactory<Grupo> specificationFactory;

    @BeforeEach
    public void setUp() throws Exception {
        mockAuthenticationForAuditing(MOCK_LOGGED_USERNAME);
    }

    @AfterEach
    public void tearDown() throws Exception {
        grupoRepository.findAll().stream()
                .filter(g -> !g.isAdmin())
                .forEach(g -> grupoRepository.delete(g));
    }

    @Test
    public void testFindByIdAdministrador() {
        Grupo grupo = grupoService.findById(Grupo.ID_ADMIN);

        assertIsAdmin(grupo);
    }

    @Test
    public void testFindAllByPageable() {
        Page<Grupo> gruposPage = grupoService.findAll(PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 1, 1, 1);
        assertIsAdmin(gruposPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterById() {
        createAndSaveGrupo("Gerência");
        createAndSaveGrupo("Recepção");

        GrupoFilterRequestTO grupoFilterRequestTO = new GrupoFilterRequestTO();
        grupoFilterRequestTO.setId(1L);

        Page<Grupo> gruposPage = grupoService.findAll(specificationFactory.create(grupoFilterRequestTO), PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 1, 1, 1);
        assertIsAdmin(gruposPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByNome() {
        createAndSaveGrupo("Gerência");
        createAndSaveGrupo("Recepção");

        GrupoFilterRequestTO grupoFilterRequestTO = new GrupoFilterRequestTO();
        grupoFilterRequestTO.setNome("recepcao");

        Page<Grupo> gruposPage = grupoService.findAll(specificationFactory.create(grupoFilterRequestTO), PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 1, 1, 1);
        assertThat(gruposPage.getContent().get(0).getNome()).isEqualTo("Recepção");
    }

    @Test
    public void testFindAllBySpecificationAndPageable_filterByAtivo() {
        createAndSaveGrupo("Gerência");
        createAndSaveGrupo("Recepção", false);

        GrupoFilterRequestTO grupoFilterRequestTO = new GrupoFilterRequestTO();
        grupoFilterRequestTO.setAtivo(true);

        Page<Grupo> gruposPage = grupoService.findAll(specificationFactory.create(grupoFilterRequestTO), PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 2, 1, 2);
        assertThat(gruposPage.getContent().stream()
                .anyMatch(g -> !g.getAtivo())).isFalse();
    }

    @Test
    public void testFindAllBySpecificationAndPageable_notFound() {
        createAndSaveGrupo("Gerência");
        createAndSaveGrupo("Recepção");

        GrupoFilterRequestTO grupoFilterRequestTO = new GrupoFilterRequestTO();
        grupoFilterRequestTO.setNome("recursos humanos");

        Page<Grupo> gruposPage = grupoService.findAll(specificationFactory.create(grupoFilterRequestTO), PageRequest.of(0, 10));

        assertPageNoContent(gruposPage, 10, 0);
    }

    @Test
    public void testFindAllActives() {
        createAndSaveGrupo("Recepção", true);
        createAndSaveGrupo("Gerência", true);
        createAndSaveGrupo("Recursos Humanos", false);
        createAndSaveGrupo("Vendas", true);

        List<Grupo> grupos = grupoService.findAllActives();

        assertThat(grupos.size()).isEqualTo(4);
    }

    @Test
    public void testSave() {
        Grupo grupo = createAndSaveGrupo("Recepção", Arrays.asList(3L, 4L, 5L), true);

        assertThat(grupo.getId()).isGreaterThan(1L);
        assertThat(grupo.getNome()).isEqualTo("Recepção");
        assertThat(grupo.getPermissoes().size()).isEqualTo(3);
        assertThat(grupo.getAtivo()).isTrue();
        assertAuditingFields(grupo, MOCK_LOGGED_USERNAME);
    }

    @Test
    public void testUpdate() {
        Long idGrupo = createAndSaveGrupo("Recepção", false).getId();

        Grupo grupo = createGrupo("Recepção", Arrays.asList(3L, 4L, 5L), true);
        grupo = grupoService.update(idGrupo, grupo);

        assertThat(grupo.getId()).isEqualTo(idGrupo);
        assertThat(grupo.getNome()).isEqualTo("Recepção");
        assertThat(grupo.getPermissoes().size()).isEqualTo(3);
        assertThat(grupo.getAtivo()).isTrue();
        assertAuditingFields(grupo, MOCK_LOGGED_USERNAME);
    }

    @Test
    public void testUpdate_whenNotFound() {
        Grupo grupo = createGrupo("Recepção", false);

        Assertions.assertThrows(InformationNotFoundException.class, () -> grupoService.update(99L, grupo));
    }

    @Test
    public void testSwitchActive() {
        Long idGrupo = createAndSaveGrupo("Recepção", Arrays.asList(3L, 4L, 5L), false).getId();

        Grupo grupo = grupoService.switchActive(idGrupo);

        assertThat(grupo.getId()).isEqualTo(idGrupo);
        assertThat(grupo.getNome()).isEqualTo("Recepção");
        assertThat(grupo.getPermissoes().size()).isEqualTo(3);
        assertThat(grupo.getAtivo()).isTrue();
        assertAuditingFields(grupo, MOCK_LOGGED_USERNAME);
    }

    @Test
    public void testSwitchActive_whenNotFound() {
        Assertions.assertThrows(InformationNotFoundException.class, () -> grupoService.switchActive(99L));
    }

    private void assertIsAdmin(Grupo grupo) {
        assertThat(grupo.getId()).isEqualTo(Grupo.ID_ADMIN);
        assertThat(grupo.getNome()).isEqualTo("Administrador");
        assertThat(grupo.getPermissoes().stream()
                .anyMatch(Permissao::hasAdmin)).isTrue();
    }

    private Grupo createGrupo(String nome) {
        Grupo grupo = new Grupo();
        grupo.setNome(nome);

        return grupo;
    }

    private Grupo createAndSaveGrupo(String nome) {
        return grupoRepository.save(createGrupo(nome));
    }

    private Grupo createGrupo(String nome, Boolean ativo) {
        Grupo grupo = new Grupo();
        grupo.setNome(nome);
        grupo.setAtivo(ativo);

        return grupo;
    }

    private Grupo createAndSaveGrupo(String nome, Boolean ativo) {
        return grupoRepository.save(createGrupo(nome, ativo));
    }

    private Grupo createGrupo(String nome, List<Long> idsPermissoes, Boolean ativo) {
        List<Permissao> permissoes = permissaoRepository.findAllById(idsPermissoes);

        Grupo grupo = new Grupo();
        grupo.setNome(nome);
        grupo.setAtivo(ativo);
        grupo.setPermissoes(new HashSet<>(permissoes));

        return grupo;
    }
    
    private Grupo createAndSaveGrupo(String nome, List<Long> idsPermissoes, Boolean ativo) {
        return grupoRepository.save(createGrupo(nome, idsPermissoes, ativo));
    }

}
