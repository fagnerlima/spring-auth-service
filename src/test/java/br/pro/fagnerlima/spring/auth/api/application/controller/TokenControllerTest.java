package br.pro.fagnerlima.spring.auth.api.application.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2SecurityService;
import io.restassured.response.Response;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TokenControllerTest extends BaseControllerTest {

    private static final String BASE_PATH = "/tokens";

    @MockBean
    private OAuth2SecurityService oauth2SecurityServiceMock;

    @Autowired
    public TokenControllerTest(OAuth2Properties oauth2Properties) {
        super(oauth2Properties);
    }

    @Test
    public void testRevoke() {
        doNothing().when(oauth2SecurityServiceMock).removeCookieRefreshToken(any(), any());

        Response response = given()
                .auth().oauth2(givenAccessTokenAsAdmin())
                .when().delete(buildUrl(BASE_PATH, "revoke"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
