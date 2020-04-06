package br.pro.fagnerlima.spring.auth.api.infrastructure.security.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.JwtProperties;
import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.exception.CustomOAuth2Exception;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.token.CustomTokenEnhancer;

@Configuration
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private AuthenticationManager authenticationManager;

    private OAuth2UserDetailsService userDetailsService;

    private OAuth2Properties oauth2Properties;

    private JwtProperties jwtProperties;

    public OAuth2AuthorizationServerConfiguration(AuthenticationManager authenticationManager, OAuth2UserDetailsService userDetailsService,
            OAuth2Properties oauth2Properties, JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.oauth2Properties = oauth2Properties;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // TODO salvar em banco de dados
        clients.inMemory()
                .withClient(oauth2Properties.getClient())
                .secret(oauth2Properties.getSecret())
                .scopes("read", "write")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(oauth2Properties.getAccessToken().getValiditySeconds())
                .refreshTokenValiditySeconds(oauth2Properties.getRefreshToken().getValiditySeconds());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(List.of(tokenEnhancer(), accessTokenConverter()));

        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .reuseRefreshTokens(false)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .exceptionTranslator(exception -> exceptionTranslator(exception));
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(jwtProperties.getSigningKey());

        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer(userDetailsService);
    }

    private ResponseEntity<OAuth2Exception> exceptionTranslator(Exception exception) throws Exception {
        if (exception instanceof OAuth2Exception) {
            OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;

            return ResponseEntity
                    .status(oAuth2Exception.getHttpErrorCode())
                    .body(new CustomOAuth2Exception(oAuth2Exception.getMessage()));
        }

        throw exception;
    }

}
