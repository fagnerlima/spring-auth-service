package br.pro.fagnerlima.spring.auth.api.domain.model.permissao;

public enum Papel {

    ROLE_ROOT,
    ROLE_SYSTEM,
    ROLE_ADMIN,
    ROLE_GRUPO_LISTAR,
    ROLE_GRUPO_BUSCAR,
    ROLE_GRUPO_SALVAR,
    ROLE_GRUPO_EDITAR,
    ROLE_GRUPO_ALTERAR_STATUS,
    ROLE_USUARIO_LISTAR,
    ROLE_USUARIO_BUSCAR,
    ROLE_USUARIO_SALVAR,
    ROLE_USUARIO_EDITAR,
    ROLE_USUARIO_ALTERAR_STATUS,
    ROLE_PERMISSAO_LISTAR,
    ROLE_PERMISSAO_BUSCAR;

    public Boolean isRoot() {
        return this.equals(ROLE_ROOT);
    }

    public Boolean isSystem() {
        return this.equals(ROLE_SYSTEM);
    }

    public Boolean isAdmin() {
        return this.equals(ROLE_ADMIN);
    }

}
