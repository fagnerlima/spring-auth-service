package br.pro.fagnerlima.spring.auth.api.application.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties("auth-service.security.oauth2")
public class OAuth2Properties {

    private BasicProperties basic;

    private String client;

    private String secret;

    private AccessTokenProperties accessToken;

    private RefreshTokenProperties refreshToken;

    public BasicProperties getBasic() {
        return basic;
    }

    public void setBasic(BasicProperties basic) {
        this.basic = basic;
    }

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

    public static class BasicProperties {

        private String username;

        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

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
