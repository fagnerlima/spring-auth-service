package br.pro.fagnerlima.spring.auth.api.infrastructure.factory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.email.MailRequestTO;

@Component
public class MailFactory {

    private final String TEMPLATE_CADASTRO_USUARIO = "mail/cadastro-usuario";
    private final String SUBJECT_CADASTRO_USUARIO = "Cadastro de Usuário";

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${auth-service.app-base-url}")
    private String appBaseUrl;

    public MailRequestTO createCadastroUsuario(Usuario usuario) {
        String template = TEMPLATE_CADASTRO_USUARIO;

        Map<String, Object> data = new HashMap<>();
        data.put("usuario", usuario);

        String text = createText(template, data);

        return new MailRequestTO(usuario.getEmail(), SUBJECT_CADASTRO_USUARIO, text);
    }

    private String createText(String template, Map<String, Object> data) {
        Context context = new Context(new Locale("pt", "BR"));
        data.put("appBaseUrl", appBaseUrl);
        data.entrySet().forEach(d -> context.setVariable(d.getKey(), d.getValue()));

        return templateEngine.process(template, context);
    }

}
