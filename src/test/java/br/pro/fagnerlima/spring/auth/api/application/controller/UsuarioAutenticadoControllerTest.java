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
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.test.util.UsuarioTestUtils;
import io.restassured.response.Response;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UsuarioAutenticadoControllerTest extends BaseControllerTest {

    private static final String BASE_PATH = "/me";

    @MockBean
    private UsuarioService usuarioServiceMock;

    private Usuario usuarioAdminMock;

    @Autowired
    public UsuarioAutenticadoControllerTest(OAuth2Properties oauth2Properties) {
        super(oauth2Properties);
    }

    @BeforeEach
    public void setUp() throws Exception {
        usuarioAdminMock = UsuarioTestUtils.createUsuarioAdminMock();
    }

    @Test
    public void testFind() {
        when(usuarioServiceMock.getAutenticado()).thenReturn(usuarioAdminMock);

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().get(buildUrl(BASE_PATH));

        response.then()
                .statusCode(HttpStatus.OK.value()).assertThat();

        assertUsuarioResponseTO(response, usuarioAdminMock);
    }

    @Test
    public void testFind_whenUnauthorized() {
        given()
                .when().get(buildUrl(BASE_PATH))
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value()).assertThat();
    }

    private void assertUsuarioResponseTO(Response response, Usuario usuario) {
        response.then()
                .body("id", equalTo(usuario.getId().intValue()))
                .body("nome", equalTo(usuario.getNome()))
                .body("email", equalTo(usuario.getEmail()))
                .body("login", equalTo(usuario.getLogin()))
                .body("pendente", equalTo(usuario.getPendente()))
                .body("bloqueado", equalTo(usuario.getBloqueado()))
                .body("ativo", equalTo(usuario.getAtivo()));

        List<Grupo> grupos = new ArrayList<>(usuario.getGrupos());

        response.then()
                .body("grupos.size()", equalTo(1))
                .body("grupos[0].id", equalTo(grupos.get(0).getId().intValue()))
                .body("grupos[0].nome", equalTo(grupos.get(0).getNome()))
                .body("grupos[0].ativo", equalTo(grupos.get(0).getAtivo()));
    }

}
