package br.pro.fagnerlima.spring.auth.api.infrastructure.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        // TODO implementar MessageService
        Usuario usuario = usuarioOpt.orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senha incorretos"));

        return new User(email, usuario.getSenha().getValor(), getAuthorities(usuario));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        usuario.getGrupos().forEach(grupo -> grupo.getPermissoes()
                .forEach(permissao -> authorities.add(new SimpleGrantedAuthority(permissao.getPapel().toUpperCase()))));

        return authorities;
    }

}
