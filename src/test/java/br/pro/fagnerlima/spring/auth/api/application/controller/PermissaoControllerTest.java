package br.pro.fagnerlima.spring.auth.api.application.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.PermissaoService;
import br.pro.fagnerlima.spring.auth.api.test.util.PermissaoTestUtils;
import io.restassured.response.Response;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PermissaoControllerTest extends BaseControllerTest {

    private static final String BASE_PATH = "/permissoes";

    @MockBean
    private PermissaoService permissaoServiceMock;

    private List<Permissao> permissaoListMock;

    private Permissao permissaoAdminMock;

    @Autowired
    public PermissaoControllerTest(OAuth2Properties oauth2Properties) {
        super(oauth2Properties);
    }

    @BeforeEach
    public void setUp() throws Exception {
        permissaoAdminMock = PermissaoTestUtils.createPermissaoAdminMock();

        permissaoListMock = new ArrayList<>();
        permissaoListMock.add(permissaoAdminMock);
    }

    @Test
    public void testFindAllActive() {
        when(permissaoServiceMock.findAllActive()).thenReturn(permissaoListMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH, "ativos"));

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat()
                .body("size()", equalTo(permissaoListMock.size()))
                .body("[0].id", equalTo(permissaoAdminMock.getId().intValue()))
                .body("[0].papel", equalTo(permissaoAdminMock.getPapel().name()))
                .body("[0].descricao", equalTo(permissaoAdminMock.getDescricao()))
                .body("[0].links.size()", equalTo(1));
    }

}
