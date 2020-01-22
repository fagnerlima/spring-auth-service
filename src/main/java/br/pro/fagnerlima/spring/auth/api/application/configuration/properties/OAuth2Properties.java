package br.pro.fagnerlima.spring.auth.api.application.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties("auth-service.security.oauth2")
public class OAuth2Properties {

    private AccessTokenProperties accessToken;

    private RefreshTokenProperties refreshToken;

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

        private Boolean secureCookie;

        private Integer validitySeconds;

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
