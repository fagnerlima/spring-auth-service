package br.pro.fagnerlima.spring.auth.api.domain.service;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;

public interface UsuarioService extends BaseService<Usuario> {

    Usuario findByEmail(String email);

    Usuario findBySenhaResetToken(String resetToken);

    Usuario updateSenhaByResetToken(String resetToken, String senha);

    void recoverLogin(String email);

    void recoverSenha(String email);

    Usuario getAutenticado();

    Usuario updateAutenticado(Usuario usuario);

    Usuario updateSenhaAutenticado(String senhaAtual, String senhaNova);

}
