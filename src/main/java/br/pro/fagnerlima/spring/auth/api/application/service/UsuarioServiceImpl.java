package br.pro.fagnerlima.spring.auth.api.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.DuplicateKeyException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidActualPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidTokenException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.NotAuthenticatedUserException;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Senha;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.factory.MailFactory;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.UsuarioRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.util.PasswordGeneratorUtils;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MailService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MessageService;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario> implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailFactory mailFactory;

    @Autowired
    private MessageService messageService;

    @Transactional(readOnly = true)
    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmailContainingIgnoreCase(email)
                .orElseThrow(() -> new InformationNotFoundException(messageService.getMessage("resource.information-not-found")));
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findBySenhaResetToken(String resetToken) {
        return usuarioRepository.findBySenhaResetToken(resetToken).orElseThrow(() -> new InvalidTokenException());
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario getAutenticado() {
        try {
            return getUserDetailsService().getUsuarioAuth().getUsuario();
        } catch (Exception exception) {
            throw new NotAuthenticatedUserException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findAllActives() {
        return usuarioRepository.findByAtivoAndPendenteAndBloqueado(true, false, false);
    }

    @Override
    public Usuario save(Usuario usuario) {
        checkUniqueFields(usuario);

        usuario.setSenha(new Senha());
        usuario.getSenha().generateResetToken();
        usuario.setEmail(usuario.getEmail().toLowerCase());
        usuario.setLogin(usuario.getLogin().toLowerCase());
        usuario.setPendente(true);
        usuario.setBloqueado(false);

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
            usuario.setEmail(usuario.getEmail().toLowerCase());
        }

        if (!usuario.getLogin().equalsIgnoreCase(usuarioSaved.getLogin())) {
            checkUniqueLogin(usuario);
            usuario.setLogin(usuario.getLogin().toLowerCase());

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

        Usuario usuarioUpdated = super.update(usuario.getId(), usuario);

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
            throw new DuplicateKeyException("usuario.duplicate-key.email");
        });
    }

    @Transactional(readOnly = true)
    private void checkUniqueLogin(Usuario usuario) {
        usuarioRepository.findByLoginContainingIgnoreCase(usuario.getLogin()).ifPresent(u -> {
            throw new DuplicateKeyException("usuario.duplicate-key.login");
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
