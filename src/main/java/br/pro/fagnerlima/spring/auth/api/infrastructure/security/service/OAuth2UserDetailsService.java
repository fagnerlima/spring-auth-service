package br.pro.fagnerlima.spring.auth.api.infrastructure.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioBloqueadoException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioInativoException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioPendenteException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioSemGrupoAtivoException;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.auth.UsuarioAuth;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.exception.UnauthenticatedException;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MessageService;

@Service
public class OAuth2UserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MessageService messageService;

    @Override
    public UsuarioAuth loadUserByUsername(String login) throws UsernameNotFoundException {
        try {
            Usuario usuario = usuarioService.findByLogin(login);
            validateUsuario(usuario);

            return new UsuarioAuth(usuario, getAuthorities(usuario));
        } catch (InformationNotFoundException informationNotFoundException) {
            throw new UsernameNotFoundException("Usuário e/ou senha incorretos"); // TODO externalizar
        }
    }

    public Boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.getPrincipal() instanceof String;
    }

    public String getUsernameAuth() {
        if (!isAuthenticated()) {
            throw new UnauthenticatedException();
        }

        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UsuarioAuth getUsuarioAuth() {
        return loadUserByUsername(getUsernameAuth());
    }

    public UsuarioAuth getUsuarioAuth(OAuth2Authentication authentication) {
        return (UsuarioAuth) authentication.getPrincipal();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        usuario.getGrupos().stream()
                .filter(g -> g.getAtivo())
                .forEach(g -> g.getPermissoes()
                        .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getPapel().name()))));

        return authorities;
    }

    private void validateUsuario(Usuario usuario) {
        if (!usuario.getAtivo()) {
            throw new UsuarioInativoException(messageService.getMessage("usuario.inativo"));
        }

        long countGruposAtivos = usuario.getGrupos().stream().filter(g -> g.getAtivo()).count();

        if (countGruposAtivos == 0) {
            throw new UsuarioSemGrupoAtivoException(messageService.getMessage("usuario.sem-grupo-ativo"));
        }

        if (usuario.getPendente()) {
            throw new UsuarioPendenteException(messageService.getMessage("usuario.pendente"));
        }

        if (usuario.getBloqueado()) {
            throw new UsuarioBloqueadoException(messageService.getMessage("usuario.bloqueado"));
        }
    }

}
