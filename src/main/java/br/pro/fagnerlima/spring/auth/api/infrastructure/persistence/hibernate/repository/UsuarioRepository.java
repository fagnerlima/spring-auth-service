package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository;

import java.util.Optional;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;

public interface UsuarioRepository extends BaseRepository<Usuario> {

    Optional<Usuario> findByEmail(String email);

}
