package br.pro.fagnerlima.spring.auth.api.infrastructure.security.handler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import br.pro.fagnerlima.spring.auth.api.application.configuration.OAuth2Properties;

@ControllerAdvice
public class OAuth2SecurityHandler implements ResponseBodyAdvice<OAuth2AccessToken> {

    private static final String ACCESS_TOKEN_METHOD_NAME = "postAccessToken";
    private static final String ACCESS_TOKEN_RESOURCE = "/oauth/token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Autowired
    private OAuth2Properties oauth2Properties;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethod().getName().equals(ACCESS_TOKEN_METHOD_NAME);
    }

    @Override
    public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();

        String refreshToken = body.getRefreshToken().getValue();
        addCookieRefreshToken(refreshToken, httpServletRequest, httpServletResponse);
        deleteBodyRefreshToken((DefaultOAuth2AccessToken) body);

        return body;
    }

    private void addCookieRefreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(oauth2Properties.getRefreshToken().getSecureCookie());
        refreshTokenCookie.setPath(request.getContextPath() + ACCESS_TOKEN_RESOURCE);
        refreshTokenCookie.setMaxAge(oauth2Properties.getRefreshToken().getValiditySeconds());
        response.addCookie(refreshTokenCookie);
    }

    private void deleteBodyRefreshToken(DefaultOAuth2AccessToken token) {
        token.setRefreshToken(null);
    }

}
