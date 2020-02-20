package br.pro.fagnerlima.spring.auth.api.application.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties("auth-service.security")
public class SecurityProperties {

    private Integer maximumAttemptsLogin;

    public Integer getMaximumAttemptsLogin() {
        return maximumAttemptsLogin;
    }

    public void setMaximumAttemptsLogin(Integer maximumAttemptsLogin) {
        this.maximumAttemptsLogin = maximumAttemptsLogin;
    }

}
