package br.pro.fagnerlima.spring.auth.api.infrastructure.security.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.pro.fagnerlima.spring.auth.api.infrastructure.security.serializer.CustomOAuth2ExceptionSerializer;

@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomOAuth2Exception extends OAuth2Exception {

    private static final long serialVersionUID = 5910676707860488741L;

    public CustomOAuth2Exception(String msg) {
        super(msg);
    }

}
