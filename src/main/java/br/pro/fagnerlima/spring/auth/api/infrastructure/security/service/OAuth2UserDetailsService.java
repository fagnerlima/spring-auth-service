package br.pro.fagnerlima.spring.auth.api.infrastructure.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.UsuarioRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.auth.UsuarioAuth;

@Service
public class OAuth2UserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UsuarioAuth loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(login);
        // TODO implementar MessageService e ApplicationExceptionHandler
        Usuario usuario = usuarioOpt.orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senha incorretos"));

        return new UsuarioAuth(usuario, getAuthorities(usuario));
    }

    public UsuarioAuth getUsuarioAuth() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return loadUserByUsername(username);
    }

    public UsuarioAuth getUsuarioAuth(OAuth2Authentication authentication) {
        return (UsuarioAuth) authentication.getPrincipal();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        usuario.getGrupos().forEach(grupo -> grupo.getPermissoes()
                .forEach(permissao -> authorities.add(new SimpleGrantedAuthority(permissao.getPapel().toUpperCase()))));

        return authorities;
    }

}
