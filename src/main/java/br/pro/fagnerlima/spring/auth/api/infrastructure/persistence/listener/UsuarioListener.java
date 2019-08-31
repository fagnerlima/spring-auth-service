package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.listener;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;

public class UsuarioListener {

    @PrePersist
    public void onPrePersist(Usuario usuario) {
        setCase(usuario);
    }

    @PreUpdate
    public void onPreUpdate(Usuario usuario) {
        setCase(usuario);
    }

    private void setCase(Usuario usuario) {
        usuario.setEmail(usuario.getEmail().toLowerCase());
        usuario.setLogin(usuario.getLogin().toLowerCase());
    }

}
