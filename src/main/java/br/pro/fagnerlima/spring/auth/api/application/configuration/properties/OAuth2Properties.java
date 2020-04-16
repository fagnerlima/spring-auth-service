package br.pro.fagnerlima.spring.auth.api.application.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties("auth-service.security.oauth2")
public class OAuth2Properties {

    private String client;

    private String secret;

    private String scopes;

    private String authorizedGrantTypes;

    private AccessTokenProperties accessToken;

    private RefreshTokenProperties refreshToken;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public AccessTokenProperties getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessTokenProperties accessToken) {
        this.accessToken = accessToken;
    }

    public RefreshTokenProperties getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshTokenProperties refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static class AccessTokenProperties {

        private Integer validitySeconds;

        public Integer getValiditySeconds() {
            return validitySeconds;
        }

        public void setValiditySeconds(Integer validitySeconds) {
            this.validitySeconds = validitySeconds;
        }
    }

    public static class RefreshTokenProperties {

        private Boolean enabled;

        private Boolean secureCookie;

        private Integer validitySeconds;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean getSecureCookie() {
            return secureCookie;
        }

        public Integer getValiditySeconds() {
            return validitySeconds;
        }

        public void setSecureCookie(Boolean secureCookie) {
            this.secureCookie = secureCookie;
        }

        public void setValiditySeconds(Integer validitySeconds) {
            this.validitySeconds = validitySeconds;
        }
    }

}
