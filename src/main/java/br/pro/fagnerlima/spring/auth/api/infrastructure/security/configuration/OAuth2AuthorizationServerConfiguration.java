package br.pro.fagnerlima.spring.auth.api.infrastructure.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import br.pro.fagnerlima.spring.auth.api.application.configuration.JwtProperties;
import br.pro.fagnerlima.spring.auth.api.application.configuration.OAuth2Properties;

@Configuration
@EnableAuthorizationServer
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
                .secret("$2a$10$Uw1cy2aulRDtBVdIRGlUnegP3MtK9SIDIVeLP29aChu47.B6YWwky") // @ngu1@r
                .scopes("read", "write")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(oauth2Properties.getAccessToken().getValiditySeconds())
                .refreshTokenValiditySeconds(oauth2Properties.getRefreshToken().getValiditySeconds());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
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

}
