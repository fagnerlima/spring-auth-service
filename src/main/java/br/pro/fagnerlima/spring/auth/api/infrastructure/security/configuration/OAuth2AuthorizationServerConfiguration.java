package br.pro.fagnerlima.spring.auth.api.infrastructure.security.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.token.CustomTokenEnhancer;

@Configuration
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OAuth2Properties oauth2Properties;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // TODO salvar em banco de dados
        clients.inMemory()
                .withClient("angular")
                .secret("$2a$10$3yrT0M2xU9Qj24Mqv4xWkekbqLYm7p3nMeg4QPcHkOajGSG8rwsj2")
                .scopes("read", "write")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(oauth2Properties.getAccessToken().getValiditySeconds())
                .refreshTokenValiditySeconds(oauth2Properties.getRefreshToken().getValiditySeconds());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .reuseRefreshTokens(false)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
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
        return new CustomTokenEnhancer();
    }

}
