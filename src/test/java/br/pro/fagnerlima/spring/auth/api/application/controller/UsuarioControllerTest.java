package br.pro.fagnerlima.spring.auth.api.application.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioResponseTO;
import br.pro.fagnerlima.spring.auth.api.test.util.ControllerTestUtils;
import br.pro.fagnerlima.spring.auth.api.test.util.GrupoTestUtils;
import br.pro.fagnerlima.spring.auth.api.test.util.UsuarioTestUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UsuarioControllerTest extends BaseControllerTest {

    private static final String BASE_PATH = "/usuarios";

    @MockBean
    private UsuarioService usuarioServiceMock;

    private SpecificationFactory<Usuario> specificationFactoryMock;

    private List<Usuario> usuarioListMock;

    private Page<Usuario> usuarioPageMock;

    private Usuario usuarioAdminMock;

    private Grupo grupoAdminMock;

    @Autowired
    public UsuarioControllerTest(OAuth2Properties oauth2Properties) {
        super(oauth2Properties);
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() throws Exception {
        specificationFactoryMock = mock(SpecificationFactory.class);

        grupoAdminMock = GrupoTestUtils.createGrupoAdminMock();
        usuarioAdminMock = UsuarioTestUtils.createUsuarioAdminMock();

        usuarioListMock = new ArrayList<>();
        usuarioListMock.add(usuarioAdminMock);

        for (Long i = 2L; i <= 8; i++) {
            usuarioListMock.add(UsuarioTestUtils.createUsuario(i, RandomStringUtils.random(10), RandomStringUtils.random(10), true,
                    Set.of(grupoAdminMock)));
        }

        usuarioPageMock = new PageImpl<>(usuarioListMock, PageRequest.of(0, 10), 8);

        when(specificationFactoryMock.create(any(), any(Class.class))).thenReturn(Specification.where(null));
    }

    @Test
    public void testFindAll() {
        when(usuarioServiceMock.findAll(any(), any())).thenReturn(usuarioPageMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH));

        ControllerTestUtils.assertPage(response, 10, 0, 8, 1, 8);

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat()
                .body("content[0].id", equalTo(usuarioAdminMock.getId().intValue()))
                .body("content[0].nome", equalTo(usuarioAdminMock.getNome()))
                .body("content[0].login", equalTo(usuarioAdminMock.getLogin()))
                .body("content[0].pendente", equalTo(usuarioAdminMock.getPendente()))
                .body("content[0].bloqueado", equalTo(usuarioAdminMock.getBloqueado()))
                .body("content[0].ativo", equalTo(usuarioAdminMock.getAtivo()))
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
        when(usuarioServiceMock.findById(any())).thenReturn(usuarioAdminMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH, 1));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        assertUsuarioResponseTO(response, usuarioAdminMock);
    }

    @Test
    public void testFindById_whenNotFound() {
        when(usuarioServiceMock.findById(any())).thenThrow(InformationNotFoundException.class);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH, 100));

        response.then()
                .statusCode(HttpStatus.NOT_FOUND.value()).assertThat();
    }

    @Test
    public void testFindAllActive() {
        when(usuarioServiceMock.findAllActive()).thenReturn(usuarioListMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH, "ativos"));

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat()
                .body("size()", equalTo(usuarioListMock.size()))
                .body("[0].id", equalTo(usuarioAdminMock.getId().intValue()))
                .body("[0].nome", equalTo(usuarioAdminMock.getNome()))
                .body("[0].login", equalTo(usuarioAdminMock.getLogin()))
                .body("[0].links.size()", equalTo(3));
    }

    @Test
    public void testSave() {
        Usuario usuarioMock = UsuarioTestUtils.createUsuario(21L, "Test", "user.test", true, Set.of(grupoAdminMock));
        when(usuarioServiceMock.save(any())).thenReturn(usuarioMock);

        String requestBody = "{\n" + 
                "  \"nome\": \"Test\",\n" + 
                "  \"email\": \"test@email.com\",\n" + 
                "  \"login\": \"user.test\",\n" + 
                "  \"grupos\": [\n" + 
                "    1\n" + 
                "  ],\n" + 
                "  \"ativo\": true\n" + 
                "}";
        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post(buildUrl(BASE_PATH));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        assertUsuarioResponseTO(response, usuarioMock);
    }

    @Test
    public void testUpdate() {
        Usuario usuarioMock = UsuarioTestUtils.createUsuario(21L, "Test", "user.test", true, Set.of(grupoAdminMock));
        when(usuarioServiceMock.update(any(), any())).thenReturn(usuarioMock);

        String requestBody = "{\n" + 
                "  \"nome\": \"Test\",\n" + 
                "  \"email\": \"test@email.com\",\n" + 
                "  \"login\": \"user.test\",\n" + 
                "  \"grupos\": [\n" + 
                "    1\n" + 
                "  ],\n" + 
                "  \"ativo\": true\n" + 
                "}";
        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().put(buildUrl(BASE_PATH, usuarioMock.getId().intValue()));

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat();

        assertUsuarioResponseTO(response, usuarioMock);
    }

    @Test
    public void testUpdateSenhaByResetToken() {
        when(usuarioServiceMock.updateSenhaByResetToken(any(), any())).thenReturn(usuarioAdminMock);

        String requestBody = "{\n" + 
                "  \"token\": \"1234-56-7890\",\n" + 
                "  \"senha\": \"P@ss3210\"\n" + 
                "}";
        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().patch(buildUrl(BASE_PATH, "senha"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testSwitchActive() {
        Usuario usuarioMock = UsuarioTestUtils.createUsuario(21L, "Test", "user.test", true, Set.of(grupoAdminMock));
        when(usuarioServiceMock.switchActive(any())).thenReturn(usuarioMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().patch(buildUrl(BASE_PATH, usuarioMock.getId().intValue(), "ativo"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testRecoverLogin() {
        doNothing().when(usuarioServiceMock).recoverLogin(any());

        String requestBody = "{\n" + 
                "  \"email\": \"user.test@email.com\"\n" + 
                "}";
        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post(buildUrl(BASE_PATH, "recuperacao", "login"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testRecoverSenha() {
        doNothing().when(usuarioServiceMock).recoverSenha(any());

        String requestBody = "{\n" + 
                "  \"email\": \"user.test@email.com\"\n" + 
                "}";
        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post(buildUrl(BASE_PATH, "recuperacao", "senha"));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void assertUsuarioResponseTO(Response response, Usuario usuario) {
        UsuarioResponseTO usuarioResponseTO = response.then().extract().as(UsuarioResponseTO.class);

        UsuarioTestUtils.assertResponseTO(usuarioResponseTO, usuario);
    }

}
