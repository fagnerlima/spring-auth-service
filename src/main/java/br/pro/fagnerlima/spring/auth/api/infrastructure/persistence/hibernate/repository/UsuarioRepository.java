package br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository;

import java.util.List;
import java.util.Optional;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;

public interface UsuarioRepository extends BaseRepository<Usuario> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByLoginIgnoreCase(String login);

    Optional<Usuario> findByEmailOrLogin(String login, String email);

    Optional<Usuario> findBySenhaResetToken(String resetToken);

    List<Usuario> findByAtivoAndPendenteAndBloqueado(Boolean ativo, Boolean pendente, Boolean bloqueado);

}
