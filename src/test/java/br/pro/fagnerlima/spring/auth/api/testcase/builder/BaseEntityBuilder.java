package br.pro.fagnerlima.spring.auth.api.testcase.builder;

import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;

public abstract class BaseEntityBuilder<T extends BaseEntity> {

    public BaseEntityBuilder<T> withAtivo(Boolean ativo) {
        getEntity().setAtivo(ativo);
        return this;
    }

    public T build() {
        return getEntity();
    }

    protected abstract T getEntity();

}
