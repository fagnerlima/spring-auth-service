package br.pro.fagnerlima.spring.auth.api.application.service;

import static br.pro.fagnerlima.spring.auth.api.test.ServiceAssertions.assertPage;
import static br.pro.fagnerlima.spring.auth.api.test.ServiceAssertions.assertPageNoContent;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Papel;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoFilterRequestTO;

@ActiveProfiles("test")
@SpringBootTest
public class GrupoServiceImplTest {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private SpecificationFactory<Grupo> specificationFactory;

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testFindByIdAdministrador() {
        Grupo grupo = grupoService.findById(1L);

        assertIsAdmin(grupo);
    }

    @Test
    public void testFindAllByPageable() {
        Page<Grupo> gruposPage = grupoService.findAll(PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 1, 1, 1);
        assertIsAdmin(gruposPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable() {
        GrupoFilterRequestTO grupoFilterRequestTO = new GrupoFilterRequestTO();
        grupoFilterRequestTO.setId(1L);

        Page<Grupo> gruposPage = grupoService.findAll(specificationFactory.create(grupoFilterRequestTO), PageRequest.of(0, 10));

        assertPage(gruposPage, 10, 0, 1, 1, 1);
        assertIsAdmin(gruposPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_NotFound() {
        GrupoFilterRequestTO grupoFilterRequestTO = new GrupoFilterRequestTO();
        grupoFilterRequestTO.setNome("Gerente");

        Page<Grupo> gruposPage = grupoService.findAll(specificationFactory.create(grupoFilterRequestTO), PageRequest.of(0, 10));

        assertPageNoContent(gruposPage, 10, 0);
    }

    private void assertIsAdmin(Grupo grupo) {
        assertThat(grupo.getId().intValue()).isEqualTo(1);
        assertThat(grupo.getNome()).isEqualTo("Administrador");
        assertThat(grupo.getPermissoes().stream()
                .map(Permissao::getPapel)
                .anyMatch(p -> p.equals(Papel.ROLE_ADMIN))).isTrue();
    }

}
