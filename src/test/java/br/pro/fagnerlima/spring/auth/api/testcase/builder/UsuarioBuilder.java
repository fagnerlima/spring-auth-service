package br.pro.fagnerlima.spring.auth.api.testcase.builder;

import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Senha;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;

public class UsuarioBuilder {

    private Usuario usuario = new Usuario();

    public UsuarioBuilder withNome(String nome) {
        usuario.setNome(nome);
        return this;
    }

    public UsuarioBuilder withEmail(String email) {
        usuario.setEmail(email);
        return this;
    }

    public UsuarioBuilder withLogin(String login) {
        usuario.setLogin(login);
        return this;
    }

    public UsuarioBuilder withSenha(Senha senha) {
        usuario.setSenha(senha);
        return this;
    }

    public UsuarioBuilder withPendente(Boolean pendente) {
        usuario.setPendente(pendente);
        return this;
    }

    public UsuarioBuilder withBloqueado(Boolean bloqueado) {
        usuario.setBloqueado(bloqueado);
        return this;
    }

    public UsuarioBuilder withAtivo(Boolean ativo) {
        usuario.setAtivo(ativo);
        return this;
    }

    public UsuarioBuilder withGrupos(Set<Grupo> grupos) {
        usuario.setGrupos(grupos);
        return this;
    }

    public Usuario build() {
        return usuario;
    }

}
