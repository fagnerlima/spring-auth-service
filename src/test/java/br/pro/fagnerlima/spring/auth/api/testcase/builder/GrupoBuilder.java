package br.pro.fagnerlima.spring.auth.api.testcase.builder;

import java.util.Set;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;

public class GrupoBuilder extends AuditedBaseEntityBuilder<Grupo> {

    private Grupo grupo = new Grupo();

    public GrupoBuilder withNome(String nome) {
        getEntity().setNome(nome);
        return this;
    }

    public GrupoBuilder withPermissoes(Set<Permissao> permissoes) {
        getEntity().setPermissoes(permissoes);
        return this;
    }

    @Override
    protected Grupo getEntity() {
        return grupo;
    }

}
