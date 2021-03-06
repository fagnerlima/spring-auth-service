package br.pro.fagnerlima.spring.auth.api.application.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.auth.UsuarioAuth;

@Component
public class ApplicationAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private UsuarioService usuarioService;

    public ApplicationAuthenticationSuccessEventListener(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getAuthentication().getPrincipal() instanceof UsuarioAuth) {
            usuarioService.loginSucceded(((UsuarioAuth) event.getAuthentication().getPrincipal()).getUsername());
        }
    }

}
