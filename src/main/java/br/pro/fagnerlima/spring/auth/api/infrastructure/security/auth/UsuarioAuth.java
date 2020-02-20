package br.pro.fagnerlima.spring.auth.api.infrastructure.security.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;

public class UsuarioAuth extends User {

    private static final long serialVersionUID = -4018993977455638750L;

    private Usuario usuario;

    public UsuarioAuth(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
        super(usuario.getLogin(), usuario.getSenha().getValor(), usuario.getAtivo(), true, true, !usuario.getBloqueado(), authorities);
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

}
