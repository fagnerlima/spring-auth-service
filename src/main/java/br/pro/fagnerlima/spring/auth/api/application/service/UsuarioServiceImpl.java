package br.pro.fagnerlima.spring.auth.api.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.DuplicateKeyException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidActualPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidTokenException;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Senha;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.factory.MailFactory;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.UsuarioRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.util.PasswordGeneratorUtils;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MailService;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario> implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailFactory mailFactory;

    @Transactional(readOnly = true)
    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmailContainingIgnoreCase(email)
                .orElseThrow(() -> new InformationNotFoundException("Usuário não encontrado")); // TODO implementar MessageService
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findBySenhaResetToken(String resetToken) {
        return usuarioRepository.findBySenhaResetToken(resetToken).orElseThrow(() -> new InvalidTokenException());
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findAutenticado() {
        return getUserDetailsService().getUsuarioAuth().getUsuario();
    }

    @Override
    public Usuario save(Usuario usuario) {
        checkUniqueFields(usuario);
        usuario.setSenha(new Senha());
        usuario.getSenha().generateResetToken();

        Usuario usuarioSaved = super.save(usuario);

        sendMailRegistration(usuarioSaved);

        return super.save(usuarioSaved);
    }

    @Override
    public Usuario update(Long id, Usuario usuario) {
        Boolean resendMailPendente = false;
        Usuario usuarioSaved = findById(id);
        usuario.setSenha(usuarioSaved.getSenha());

        if (!usuario.getEmail().equalsIgnoreCase(usuarioSaved.getEmail())) {
            checkUniqueEmail(usuario);
        }

        if (!usuario.getLogin().equalsIgnoreCase(usuarioSaved.getLogin())) {
            checkUniqueLogin(usuario);

            if (usuarioSaved.getPendente()) {
                resendMailPendente = true;
            }
        }

        Usuario usuarioUpdated = super.update(id, usuario);

        if (resendMailPendente) {
            sendMailRegistration(usuarioUpdated);
        }

        return super.update(id, usuario);
    }

    @Override
    public Usuario updateAutenticado(Usuario usuario) {
        Long id = getUserDetailsService().getUsuarioAuth().getUsuario().getId();
        usuario.ativar();

        return update(id, usuario);
    }

    @Override
    public Usuario updateSenhaByResetToken(String resetToken, String senha) {
        Usuario usuario = findBySenhaResetToken(resetToken);
        usuario.updateSenha(PasswordGeneratorUtils.encode(senha));

        return super.update(usuario.getId(), usuario);
    }

    @Override
    public Usuario updateSenhaAutenticado(String senhaAtual, String senhaNova) {
        Usuario usuario = getUsuarioAutenticado();

        if (!PasswordGeneratorUtils.validate(senhaAtual, usuario.getSenha().getValor())) {
            throw new InvalidActualPasswordException();
        }

        usuario.updateSenha(PasswordGeneratorUtils.encode(senhaNova));

        return super.update(usuario.getId(), usuario);
    }

    @Override
    public void recoverLogin(String email) {
        Usuario usuario = findByEmail(email);

        sendMailRecoveryLogin(usuario);
    }

    @Override
    public void recoverSenha(String email) {
        Usuario usuario = findByEmail(email);
        usuario.getSenha().generateResetToken();

        Usuario usuarioUpdated = this.update(usuario.getId(), usuario);

        sendMailRecoverySenha(usuarioUpdated);
    }

    @Override
    protected BaseRepository<Usuario> getRepository() {
        return usuarioRepository;
    }

    private void checkUniqueFields(Usuario usuario) {
        checkUniqueEmail(usuario);
        checkUniqueLogin(usuario);
    }

    @Transactional(readOnly = true)
    private void checkUniqueEmail(Usuario usuario) {
        usuarioRepository.findByEmailContainingIgnoreCase(usuario.getEmail()).ifPresent(u -> {
            throw new DuplicateKeyException("E-mail já cadastrado"); // TODO implementar MessageService
        });
    }

    @Transactional(readOnly = true)
    private void checkUniqueLogin(Usuario usuario) {
        usuarioRepository.findByLoginContainingIgnoreCase(usuario.getLogin()).ifPresent(u -> {
            throw new DuplicateKeyException("Login já cadastrado"); // TODO implementar MessageService
        });
    }

    private void sendMailRegistration(Usuario usuario) {
        mailService.send(mailFactory.createRegistrationUsuario(usuario));
    }

    private void sendMailRecoveryLogin(Usuario usuario) {
        mailService.send(mailFactory.createRecoveryUsuarioLogin(usuario));
    }

    private void sendMailRecoverySenha(Usuario usuario) {
        mailService.send(mailFactory.createRecoveryUsuarioSenha(usuario));
    }

}
