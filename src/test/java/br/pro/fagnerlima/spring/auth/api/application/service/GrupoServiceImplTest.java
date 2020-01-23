package br.pro.fagnerlima.spring.auth.api.application.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Papel;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoFilterRequestTO;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class GrupoServiceImplTest {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private SpecificationFactory<Grupo> specificationFactory;

    @Before
    public void setUp() throws Exception {
    }

    @After
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

        assertPageable(gruposPage, 0, 10, 1, 1, 1);
        assertIsAdmin(gruposPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable() {
        GrupoFilterRequestTO grupoFilterRequestTO = new GrupoFilterRequestTO();
        grupoFilterRequestTO.setId(1L);

        Page<Grupo> gruposPage = grupoService.findAll(specificationFactory.create(grupoFilterRequestTO), PageRequest.of(0, 10));

        assertPageable(gruposPage, 0, 10, 1, 1, 1);
        assertIsAdmin(gruposPage.getContent().get(0));
    }

    @Test
    public void testFindAllBySpecificationAndPageable_NotFound() {
        GrupoFilterRequestTO grupoFilterRequestTO = new GrupoFilterRequestTO();
        grupoFilterRequestTO.setNome("Gerente");

        Page<Grupo> gruposPage = grupoService.findAll(specificationFactory.create(grupoFilterRequestTO), PageRequest.of(0, 10));

        assertPageable(gruposPage, 0, 10, 0, 0, 1);
    }

    private void assertIsAdmin(Grupo grupo) {
        assertEquals(grupo.getId().intValue(), 1);
        assertEquals(grupo.getNome(), "Administrador");
        assertTrue(grupo.getPermissoes().stream()
                .map(Permissao::getPapel)
                .anyMatch(p -> p.equals(Papel.ROLE_ADMIN)));
    }

    private void assertPageable(Page<?> page, int number, int size, int numberOfElements, int totalElements, int totalPages) {
        assertEquals(page.getNumber(), number);
        assertEquals(page.getSize(), size);
        assertEquals(page.getNumberOfElements(), numberOfElements);
        assertEquals(page.getTotalElements(), totalElements);
        assertEquals(page.getTotalPages(), totalPages);
        assertEquals(page.getContent().size(), numberOfElements);
    }

}
