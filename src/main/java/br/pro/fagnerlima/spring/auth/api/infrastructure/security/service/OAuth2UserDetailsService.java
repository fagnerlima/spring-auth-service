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

import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioBloqueadoException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioInativoException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioPendenteException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioSemGrupoAtivoException;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.UsuarioRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.auth.UsuarioAuth;

@Service
public class OAuth2UserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UsuarioAuth loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLoginContainingIgnoreCase(login);
        Usuario usuario = usuarioOpt.orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senha incorretos"));

        validateUsuario(usuario);

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
        usuario.getGrupos().stream()
                .filter(g -> g.getAtivo())
                .forEach(g -> g.getPermissoes()
                        .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getPapel().toUpperCase()))));

        return authorities;
    }

    private void validateUsuario(Usuario usuario) {
        if (!usuario.getAtivo()) {
            throw new UsuarioInativoException();
        }

        long countGruposAtivos = usuario.getGrupos().stream().filter(g -> g.getAtivo()).count();

        if (countGruposAtivos == 0) {
            throw new UsuarioSemGrupoAtivoException();
        }

        if (usuario.getPendente()) {
            throw new UsuarioPendenteException();
        }

        if (usuario.getBloqueado()) {
            throw new UsuarioBloqueadoException();
        }
    }

}
