package br.pro.fagnerlima.spring.auth.api.application.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Papel;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.test.util.ControllerTestUtils;
import br.pro.fagnerlima.spring.auth.api.test.util.GrupoTestUtils;
import br.pro.fagnerlima.spring.auth.api.test.util.PermissaoTestUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GrupoControllerTest extends BaseControllerTest {

    private static final String BASE_PATH = "/grupos";

    @MockBean
    private GrupoService grupoServiceMock;

    private SpecificationFactory<Grupo> specificationFactoryMock;

    private List<Grupo> grupoListMock;

    private Page<Grupo> grupoPageMock;

    private Grupo grupoAdministradorMock;

    private Permissao permissaoAdminMock;

    @Autowired
    public GrupoControllerTest(OAuth2Properties oauth2Properties) {
        super(oauth2Properties);
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() throws Exception {
        specificationFactoryMock = mock(SpecificationFactory.class);

        permissaoAdminMock = PermissaoTestUtils.createPermissao(1L, Papel.ROLE_ADMIN, "Administrador");
        grupoAdministradorMock = GrupoTestUtils.createGrupo(1L, "Administrador", true, Set.of(permissaoAdminMock));

        grupoListMock = new ArrayList<>();
        grupoListMock.add(grupoAdministradorMock);

        for (Long i = 2L; i <= 8; i++) {
            grupoListMock.add(GrupoTestUtils.createGrupo(i, RandomStringUtils.random(10), true));
        }

        grupoPageMock = new PageImpl<>(grupoListMock, PageRequest.of(0, 10), 8);

        when(specificationFactoryMock.create(any(), any(Class.class))).thenReturn(Specification.where(null));
    }

    @Test
    public void testFindAll() {
        when(grupoServiceMock.findAll(any(), any())).thenReturn(grupoPageMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH));

        ControllerTestUtils.assertPage(response, 10, 0, 8, 1, 8);

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat()
                .body("content[0].id", equalTo(grupoAdministradorMock.getId().intValue()))
                .body("content[0].nome", equalTo(grupoAdministradorMock.getNome()))
                .body("content[0].ativo", equalTo(grupoAdministradorMock.getAtivo()))
                .body("content[0].links.size()", equalTo(3));
    }

    @Test
    public void testFindAll_whenUnauthorized() {
        given()
                .when().get(buildUrl(BASE_PATH))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value()).assertThat();
    }

    @Test
    public void testFindById() {
        when(grupoServiceMock.findById(any())).thenReturn(grupoAdministradorMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH,1));

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat();

        assertGrupoResponseTO(response, grupoAdministradorMock);
    }

    @Test
    public void testFindAllActive() {
        when(grupoServiceMock.findAllActive()).thenReturn(grupoListMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH, "ativos"));

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat()
                .body("size()", equalTo(grupoListMock.size()))
                .body("[0].id", equalTo(grupoAdministradorMock.getId().intValue()))
                .body("[0].nome", equalTo(grupoAdministradorMock.getNome()));
    }

    @Test
    public void testSave() {
        Grupo grupoMock = GrupoTestUtils.createGrupo(21L, "Test", true, Set.of(permissaoAdminMock));
        when(grupoServiceMock.save(any())).thenReturn(grupoMock);

        String requestBody = "{\n" + 
                "  \"nome\": \"Test\",\n" + 
                "  \"permissoes\": [1],\n" + 
                "  \"ativo\": true\n" + 
                "}";
        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post(buildUrl(BASE_PATH));

        response.then()
                .statusCode(HttpStatus.CREATED.value()).assertThat();

        assertGrupoResponseTO(response, grupoMock);
    }

    @Test
    public void testUpdate() {
        Grupo grupoMock = GrupoTestUtils.createGrupo(21L, "Test", true, Set.of(permissaoAdminMock));
        when(grupoServiceMock.update(any(), any())).thenReturn(grupoMock);

        String requestBody = "{\n" + 
                "  \"nome\": \"Test\",\n" + 
                "  \"permissoes\": [1],\n" + 
                "  \"ativo\": true\n" + 
                "}";
        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().put(buildUrl(BASE_PATH,grupoMock.getId().intValue()));

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat();

        assertGrupoResponseTO(response, grupoMock);
    }

    @Test
    public void testSwitchActive() {
        Grupo grupoMock = GrupoTestUtils.createGrupo(21L, "Test", true, Set.of(permissaoAdminMock));
        when(grupoServiceMock.switchActive(any())).thenReturn(grupoMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().patch(buildUrl(BASE_PATH, grupoMock.getId().intValue(), "ativo"));

        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value()).assertThat();
    }

    private void assertGrupoResponseTO(Response response, Grupo grupo) {
        response.then()
                .body("id", equalTo(grupo.getId().intValue()))
                .body("nome", equalTo(grupo.getNome()))
                .body("ativo", equalTo(grupo.getAtivo()));

        List<Permissao> permissoes = new ArrayList<>(grupo.getPermissoes());

        response.then()
                .body("permissoes.size()", equalTo(permissoes.size()))
                .body("permissoes[0].id", equalTo(permissoes.get(0).getId().intValue()))
                .body("permissoes[0].papel", equalTo(permissoes.get(0).getPapel().name()))
                .body("permissoes[0].descricao", equalTo(permissoes.get(0).getDescricao()))
                .body("links.size()", equalTo(3));
    }

}
