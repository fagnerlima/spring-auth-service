package br.pro.fagnerlima.spring.auth.api.test.auth;

import static io.restassured.RestAssured.given;

import org.springframework.http.HttpStatus;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;

public class OAuth2AuthenticationTestUtils {

    public static String givenAccessToken(String url, OAuth2Properties oauth2Properties, String username, String password) {
        return given()
                .auth()
                .preemptive()
                .basic(oauth2Properties.getClient(), oauth2Properties.getSecret())
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post(url)
                .then().log().ifValidationFails()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("access_token");
    }

}
