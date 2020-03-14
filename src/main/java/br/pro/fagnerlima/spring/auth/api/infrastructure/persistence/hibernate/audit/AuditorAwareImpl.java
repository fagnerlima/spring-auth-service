package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;

public class AuditorAwareImpl implements AuditorAware<String> {

    private OAuth2UserDetailsService userDetailsService;

    public AuditorAwareImpl(OAuth2UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(userDetailsService.isAuthenticated()
                ? userDetailsService.getUsernameAuth()
                : Usuario.LOGIN_SYSTEM);
    }

}
