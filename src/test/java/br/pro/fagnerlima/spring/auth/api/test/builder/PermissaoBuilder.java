package br.pro.fagnerlima.spring.auth.api.test.builder;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Papel;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;

public class PermissaoBuilder {

    private Permissao permissao = new Permissao();

    public PermissaoBuilder withId(Long id) {
        permissao.setId(id);
        return this;
    }

    public PermissaoBuilder withPapel(Papel papel) {
        permissao.setPapel(papel);
        return this;
    }

    public PermissaoBuilder withDescricao(String descricao) {
        permissao.setDescricao(descricao);
        return this;
    }

    public Permissao build() {
        return permissao;
    }

}
