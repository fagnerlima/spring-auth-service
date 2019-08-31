package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository;

import java.util.Optional;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;

public interface UsuarioRepository extends BaseRepository<Usuario> {

    Optional<Usuario> findByEmailContainingIgnoreCase(String email);

    Optional<Usuario> findByLoginContainingIgnoreCase(String login);

    Optional<Usuario> findByEmailOrLogin(String login, String email);

    Optional<Usuario> findBySenhaResetToken(String resetToken);

}
