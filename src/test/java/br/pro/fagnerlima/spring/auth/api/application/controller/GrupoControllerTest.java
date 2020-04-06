package br.pro.fagnerlima.spring.auth.api.application.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
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
import br.pro.fagnerlima.spring.auth.api.test.auth.OAuth2AuthenticationTest;
import br.pro.fagnerlima.spring.auth.api.test.util.ControllerTestUtils;
import br.pro.fagnerlima.spring.auth.api.test.util.GrupoTestUtils;
import br.pro.fagnerlima.spring.auth.api.test.util.PermissaoTestUtils;
import io.restassured.response.Response;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GrupoControllerTest {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String TOKEN_PATH = "/oauth/token";
    private static final String BASE_PATH = "/grupos";

    private OAuth2Properties oauth2Properties;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private String accessToken;

    @MockBean
    private GrupoService grupoServiceMock;

    private SpecificationFactory<Grupo> specificationFactoryMock;

    private Grupo grupoAdministradorMock;

    @Autowired
    public GrupoControllerTest(OAuth2Properties oauth2Properties) {
        this.oauth2Properties = oauth2Properties;
    }

    @PostConstruct
    public void init() {
        baseUrl = "http://localhost:" + port;
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() throws Exception {
        accessToken = OAuth2AuthenticationTest.givenAccessToken(baseUrl + TOKEN_PATH, oauth2Properties, ADMIN_USERNAME, ADMIN_PASSWORD);
        specificationFactoryMock = mock(SpecificationFactory.class);

        Permissao permissaoAdmin = PermissaoTestUtils.createPermissao(1L, Papel.ROLE_ADMIN, "Administrador");
        grupoAdministradorMock = GrupoTestUtils.createGrupo(1L, "Administrador", true, Set.of(permissaoAdmin));

        List<Grupo> gruposList = new ArrayList<>();
        gruposList.add(grupoAdministradorMock);

        for (Long i = 2L; i <= 8; i++) {
            gruposList.add(GrupoTestUtils.createGrupo(i, RandomStringUtils.random(10), true));
        }

        Page<Grupo> gruposPage = new PageImpl<>(gruposList, PageRequest.of(0, 10), 8);

        when(specificationFactoryMock.create(any(), any(Class.class))).thenReturn(Specification.where(null));
        when(grupoServiceMock.findAll(any())).thenReturn(gruposPage);
        when(grupoServiceMock.findAll(any(), any())).thenReturn(gruposPage);
    }

    @Test
    public void testFindAll() {
        Response response = given()
                .auth().oauth2(accessToken)
                .when().get(baseUrl + BASE_PATH);

        ControllerTestUtils.assertPage(response, 10, 0, 8, 1, 8);

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat()
                .body("content[0].id", equalTo(grupoAdministradorMock.getId().intValue()))
                .body("content[0].nome", equalTo(grupoAdministradorMock.getNome()))
                .body("content[0].ativo", equalTo(grupoAdministradorMock.getAtivo()))
                .body("content[0].links", hasSize(3));
    }

}
