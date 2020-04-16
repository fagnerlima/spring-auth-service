package br.pro.fagnerlima.spring.auth.api.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.SecurityProperties;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.BusinessException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.DuplicateKeyException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidActualPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidTokenException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.NotAuthenticatedUserException;
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Senha;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.factory.MailFactory;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.UsuarioRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.UsuarioSpecification;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.util.BcryptUtils;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MailService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MessageService;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario> implements UsuarioService {

    private UsuarioRepository usuarioRepository;

    private MailService mailService;

    private MailFactory mailFactory;

    private MessageService messageService;

    private SecurityProperties securityProperties;

    public UsuarioServiceImpl(OAuth2UserDetailsService userDetailsService, UsuarioRepository usuarioRepository, MailService mailService,
            MailFactory mailFactory, MessageService messageService, SecurityProperties securityProperties) {
        super(userDetailsService);
        this.usuarioRepository = usuarioRepository;
        this.mailService = mailService;
        this.mailFactory = mailFactory;
        this.messageService = messageService;
        this.securityProperties = securityProperties;
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new InformationNotFoundException(messageService.getMessage("resource.information-not-found")));
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findByLogin(String login) {
        return usuarioRepository.findByLoginIgnoreCase(login)
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
            return getUsuarioAutenticado();
        } catch (Exception exception) {
            throw new NotAuthenticatedUserException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findAllActive() {
        return usuarioRepository.findAll(UsuarioSpecification.positiveIdAndActiveAndNotPendenteAndNotBloqueado());
    }

    @Override
    public Usuario save(Usuario usuario) {
        checkSave(usuario);

        usuario.setSenha(new Senha());
        usuario.getSenha().generateResetToken();
        usuario.setEmail(usuario.getEmail().toLowerCase());
        usuario.setLogin(usuario.getLogin().toLowerCase());
        usuario.setPendente(true);
        usuario.setBloqueado(false);

        Usuario usuarioSaved = super.save(usuario);

        sendMailRegistration(usuarioSaved);

        return usuarioSaved;
    }

    @Override
    public Usuario update(Long id, Usuario usuario) {
        Boolean resendMailPendente = false;
        Usuario usuarioSaved = findById(id);

        checkUpdate(id, usuario, usuarioSaved);

        if (!usuario.getLogin().equalsIgnoreCase(usuarioSaved.getLogin()) && usuarioSaved.getPendente()) {
            resendMailPendente = true;
        }

        usuario.setSenha(usuarioSaved.getSenha());

        Usuario usuarioUpdated = super.update(id, usuario);

        if (resendMailPendente) {
            sendMailRegistration(usuarioUpdated);
        }

        return super.update(id, usuario);
    }

    @Override
    public Usuario updateAutenticado(Usuario usuario) {
        Usuario usuarioAutenticado = getUserDetailsService().getUsuarioAuth().getUsuario();
        usuario.setGrupos(usuarioAutenticado.getGrupos());

        return update(usuarioAutenticado.getId(), usuario);
    }

    @Override
    public Usuario updateSenhaByResetToken(String resetToken, String senha) {
        Usuario usuario = findBySenhaResetToken(resetToken);
        usuario.updateSenha(BcryptUtils.encode(senha));

        return super.update(usuario.getId(), usuario);
    }

    @Override
    public Usuario updateSenhaAutenticado(String senhaAtual, String senhaNova) {
        Usuario usuario = getAutenticado();

        if (!BcryptUtils.validate(senhaAtual, usuario.getSenha().getValor())) {
            throw new InvalidActualPasswordException();
        }

        usuario.updateSenha(BcryptUtils.encode(senhaNova));

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
    public void loginFailed(String login) {
        try {
            Usuario usuario = findByLogin(login);
            usuario.getSenha().addTentativaErro();

            if (usuario.getSenha().getTentativasErro() >= securityProperties.getMaximumAttemptsLogin()) {
                usuario.bloquear();
            }

            super.update(usuario.getId(), usuario);
        } catch (InformationNotFoundException informationNotFoundException) {
        }
    }

    @Override
    public void loginSucceded(String login) {
        Usuario usuario = findByLogin(login);

        if (usuario.getSenha().getTentativasErro() == 0) {
            return;
        }

        usuario.getSenha().resetTentativasErro();

        super.update(usuario.getId(), usuario);
    }

    @Override
    public void switchBloqueado(Long id) {
        Usuario usuario = findById(id);
        usuario.switchBloqueado();

        super.update(usuario.getId(), usuario);
    }

    @Override
    protected BaseRepository<Usuario> getRepository() {
        return usuarioRepository;
    }

    private void checkSave(Usuario usuario) {
        if (usuario.getGrupos() == null) {
            return;
        }

        if (usuario.getGrupos().stream().anyMatch(Grupo::isRoot)) {
            throw new BusinessException("usuario.save.grupos.root");
        }

        if (usuario.getGrupos().stream().anyMatch(Grupo::isSystem)) {
            throw new BusinessException("usuario.save.grupos.system");
        }

        if ((!getAutenticado().isAdmin() && !getAutenticado().isRoot()) && usuario.getGrupos().stream().anyMatch(Grupo::isAdmin)) {
            throw new BusinessException("usuario.save.grupos.admin");
        }

        checkUniqueFields(usuario);
    }

    private void checkUpdate(Long id, Usuario usuario, Usuario usuarioSaved) {
        if (id == Usuario.ID_ROOT) {
            checkUpdateRoot(usuario);
        }

        if (id == Usuario.ID_SYSTEM) {
            checkUpdateSystem(usuario);
        }

        if (id == Usuario.ID_ADMIN) {
            checkUpdateAdmin(usuario);
        }

        if (!usuario.getEmail().equalsIgnoreCase(usuarioSaved.getEmail())) {
            checkUniqueEmail(usuario);
            usuario.setEmail(usuario.getEmail().toLowerCase());
        }

        if (!usuario.getLogin().equalsIgnoreCase(usuarioSaved.getLogin())) {
            checkUniqueLogin(usuario);
            usuario.setLogin(usuario.getLogin().toLowerCase());
        }
    }

    private void checkUpdateRoot(Usuario usuario) {
        if (usuario.getGrupos() == null || usuario.getGrupos().size() != 1
                || !usuario.getGrupos().stream().anyMatch(Grupo::isRoot)) {
            throw new BusinessException("usuario.update.grupos.root");
        }
    }

    private void checkUpdateSystem(Usuario usuario) {
        if (usuario.getGrupos() == null || usuario.getGrupos().size() != 1
                || !usuario.getGrupos().stream().anyMatch(Grupo::isSystem)) {
            throw new BusinessException("usuario.update.grupos.system");
        }
    }

    private void checkUpdateAdmin(Usuario usuario) {
        if (usuario.getGrupos() == null || usuario.getGrupos().size() != 1
                || !usuario.getGrupos().stream().anyMatch(Grupo::isAdmin)) {
            throw new BusinessException("usuario.update.grupos.admin");
        }
    }

    private void checkUniqueFields(Usuario usuario) {
        checkUniqueEmail(usuario);
        checkUniqueLogin(usuario);
    }

    @Transactional(readOnly = true)
    private void checkUniqueEmail(Usuario usuario) {
        usuarioRepository.findByEmailIgnoreCase(usuario.getEmail()).ifPresent(u -> {
            throw new DuplicateKeyException("usuario.duplicate-key.email");
        });
    }

    @Transactional(readOnly = true)
    private void checkUniqueLogin(Usuario usuario) {
        usuarioRepository.findByLoginIgnoreCase(usuario.getLogin()).ifPresent(u -> {
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
