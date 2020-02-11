package br.pro.fagnerlima.spring.auth.api.testcase.builder;

import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioFilterRequestTO;

public class UsuarioFilterRequestTOBuilder {

    private UsuarioFilterRequestTO usuarioFilter = new UsuarioFilterRequestTO();

    public UsuarioFilterRequestTOBuilder withId(Long id) {
        usuarioFilter.setId(id);
        return this;
    }

    public UsuarioFilterRequestTOBuilder withIds(Set<Long> ids) {
        usuarioFilter.setIds(ids);
        return this;
    }

    public UsuarioFilterRequestTOBuilder withNome(String nome) {
        usuarioFilter.setNome(nome);
        return this;
    }

    public UsuarioFilterRequestTOBuilder withEmail(String email) {
        usuarioFilter.setEmail(email);
        return this;
    }

    public UsuarioFilterRequestTOBuilder withLogin(String login) {
        usuarioFilter.setLogin(login);
        return this;
    }

    public UsuarioFilterRequestTOBuilder withAtivo(Boolean ativo) {
        usuarioFilter.setAtivo(ativo);
        return this;
    }

    public UsuarioFilterRequestTO build() {
        return usuarioFilter;
    }

}
