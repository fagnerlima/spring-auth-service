package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;

public interface PermissaoRepository extends BaseRepository<Permissao> {

    @Query("select p from Permissao p"
            + " where p.papel not in ('ROLE_SYSTEM')"
            + " order by"
            + "   case when p.id = 1 then 0 else 1 end,"
            + "   p.descricao")
    List<Permissao> findAllActives();
}
