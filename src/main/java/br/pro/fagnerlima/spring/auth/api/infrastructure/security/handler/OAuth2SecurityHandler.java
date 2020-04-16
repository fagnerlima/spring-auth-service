package br.pro.fagnerlima.spring.auth.api.infrastructure.security.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2SecurityService;

@ControllerAdvice
public class OAuth2SecurityHandler implements ResponseBodyAdvice<OAuth2AccessToken> {

    private static final String ACCESS_TOKEN_METHOD_NAME = "postAccessToken";

    private OAuth2SecurityService oauth2SecurityService;

    private OAuth2Properties oauth2Properties;

    public OAuth2SecurityHandler(OAuth2SecurityService oauth2SecurityService, OAuth2Properties oauth2Properties) {
        this.oauth2SecurityService = oauth2SecurityService;
        this.oauth2Properties = oauth2Properties;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethod().getName().equals(ACCESS_TOKEN_METHOD_NAME);
    }

    @Override
    public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();

        if (oauth2Properties.getRefreshToken().getEnabled()) {
            String refreshToken = body.getRefreshToken().getValue();
            oauth2SecurityService.addCookieRefreshToken(httpServletRequest, httpServletResponse, refreshToken);
        }

        deleteBodyRefreshToken((DefaultOAuth2AccessToken) body);

        return body;
    }

    private void deleteBodyRefreshToken(DefaultOAuth2AccessToken token) {
        token.setRefreshToken(null);
    }

}
