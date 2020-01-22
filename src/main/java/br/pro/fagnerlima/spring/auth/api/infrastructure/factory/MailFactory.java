package br.pro.fagnerlima.spring.auth.api.infrastructure.factory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.WebAppProperties;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.email.MailRequestTO;

@Component
public class MailFactory {

    private final String TEMPLATE_REGISTRATION_USUARIO = "mail/usuario-registration";
    private final String SUBJECT_REGISTRATION_USUARIO = "Cadastro de Usuário";

    private final String TEMPLATE_RECOVERY_USUARIO_LOGIN = "mail/usuario-recovery-login";
    private final String SUBJECT_RECOVERY_USUARIO_LOGIN = "Recuperação de Login";

    private final String TEMPLATE_RECOVERY_USUARIO_SENHA = "mail/usuario-recovery-senha";
    private final String SUBJECT_RECOVERY_USUARIO_SENHA = "Recuperação de Senha";

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private WebAppProperties webAppProperties;

    public MailRequestTO createRegistrationUsuario(Usuario usuario) {
        return createTemplateUsuario(TEMPLATE_REGISTRATION_USUARIO, SUBJECT_REGISTRATION_USUARIO, usuario);
    }

    public MailRequestTO createRecoveryUsuarioLogin(Usuario usuario) {
        return createTemplateUsuario(TEMPLATE_RECOVERY_USUARIO_LOGIN, SUBJECT_RECOVERY_USUARIO_LOGIN, usuario);
    }

    public MailRequestTO createRecoveryUsuarioSenha(Usuario usuario) {
        return createTemplateUsuario(TEMPLATE_RECOVERY_USUARIO_SENHA, SUBJECT_RECOVERY_USUARIO_SENHA, usuario);
    }

    private MailRequestTO createTemplateUsuario(String template, String subject, Usuario usuario) {
        Map<String, Object> data = new HashMap<>();
        data.put("usuario", usuario);

        return new MailRequestTO(usuario.getEmail(), subject, createText(template, data));
    }

    private String createText(String template, Map<String, Object> data) {
        Context context = new Context(new Locale("pt", "BR"));
        data.put("webAppBaseUrl", webAppProperties.getBaseUrl());
        data.entrySet().forEach(d -> context.setVariable(d.getKey(), d.getValue()));

        return templateEngine.process(template, context);
    }

}
