package br.pro.fagnerlima.spring.auth.api.infrastructure.security.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;

@Service
public class OAuth2SecurityService {

    private static final String TOKEN_RESOURCE = "/oauth/token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Autowired
    private OAuth2Properties oauth2Properties;

    public void addCookieRefreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        createCookieRefreshToken(request, response, refreshToken, oauth2Properties.getRefreshToken().getValiditySeconds());
    }

    public void removeCookieRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        createCookieRefreshToken(request, response, null, 0);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    private void createCookieRefreshToken(HttpServletRequest request, HttpServletResponse response, String value, Integer validitySeconds) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(oauth2Properties.getRefreshToken().getSecureCookie());
        cookie.setPath(request.getContextPath() + TOKEN_RESOURCE);
        cookie.setMaxAge(validitySeconds);

        response.addCookie(cookie);
    }

}
