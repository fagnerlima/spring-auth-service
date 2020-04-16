package br.pro.fagnerlima.spring.auth.api.application.controller;

import javax.annotation.PostConstruct;

import org.springframework.boot.web.server.LocalServerPort;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;
import br.pro.fagnerlima.spring.auth.api.test.auth.OAuth2AuthenticationTestUtils;

public abstract class BaseControllerTest {

    protected static final String ADMIN_USERNAME = "admin";
    protected static final String ADMIN_PASSWORD = "Admin@123";
    protected static final String TOKEN_PATH = "/oauth/token";

    @LocalServerPort
    private Integer port;

    private String baseUrl;

    private OAuth2Properties oauth2Properties;

    public BaseControllerTest(OAuth2Properties oauth2Properties) {
        this.oauth2Properties = oauth2Properties;
    }

    @PostConstruct
    public void init() {
        baseUrl = "http://localhost:" + port;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    protected String buildUrl(Object... args) {
        String url = baseUrl;

        for (Object arg : args) {
            url += arg.toString().startsWith("/") ? arg : "/" + arg;
        }

        return url;
    }

    protected String givenAccessToken(String username, String password) {
        return OAuth2AuthenticationTestUtils.givenAccessToken(baseUrl + TOKEN_PATH, oauth2Properties, username, password);
    }

    protected String givenAccessTokenAsAdmin() {
        return givenAccessToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

}
