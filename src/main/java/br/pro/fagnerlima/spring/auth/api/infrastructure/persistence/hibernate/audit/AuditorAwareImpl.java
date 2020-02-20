package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.audit;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Autowired
    private OAuth2UserDetailsService userDetailsService;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(userDetailsService.isAuthenticated()
                ? userDetailsService.getUsernameAuth()
                : Usuario.LOGIN_SYSTEM);
    }

}
