package br.pro.fagnerlima.spring.auth.api.infrastructure.security.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import br.pro.fagnerlima.spring.auth.api.infrastructure.security.UsuarioAuth;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private OAuth2UserDetailsService userDetailsService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        UsuarioAuth usuarioSistema = userDetailsService.getUsuarioAuth(authentication);

        Map<String, Object> info = new HashMap<>();
        info.put("nome", usuarioSistema.getUsuario().getNome());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);

        return accessToken;
    }

}
