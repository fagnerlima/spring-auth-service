package br.pro.fagnerlima.spring.auth.api.infrastructure.security.configuration;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.JwtProperties;
import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.token.CustomTokenEnhancer;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.util.BcryptUtils;

@Configuration
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private AuthenticationManager authenticationManager;

    private DataSource dataSource;

    private OAuth2UserDetailsService userDetailsService;

    private OAuth2Properties oauth2Properties;

    private JwtProperties jwtProperties;

    public OAuth2AuthorizationServerConfiguration(AuthenticationManager authenticationManager, DataSource dataSource,
            OAuth2UserDetailsService userDetailsService, OAuth2Properties oauth2Properties, JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
        this.oauth2Properties = oauth2Properties;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        try {
            clients.jdbc(dataSource)
                    .build()
                    .loadClientByClientId(oauth2Properties.getClient());
        } catch (ClientRegistrationException clientRegistrationException) {
            clients.jdbc(dataSource)
                    .withClient(oauth2Properties.getClient())
                    .secret(BcryptUtils.encode(oauth2Properties.getSecret()))
                    .scopes(oauth2Properties.getScopes())
                    .authorizedGrantTypes(oauth2Properties.getAuthorizedGrantTypes())
                    .accessTokenValiditySeconds(oauth2Properties.getAccessToken().getValiditySeconds())
                    .refreshTokenValiditySeconds(oauth2Properties.getRefreshToken().getValiditySeconds());
        }
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(List.of(tokenEnhancer(), accessTokenConverter()));

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
        return new CustomTokenEnhancer(userDetailsService);
    }

}
