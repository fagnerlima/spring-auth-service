package br.pro.fagnerlima.spring.auth.api.infrastructure.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.MailProperties;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.exception.MailException;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.email.MailRequestTO;

@Service
public class MailService {

    private JavaMailSender mailSender;

    private MailProperties mailProperties;

    public MailService(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Async
    public void send(MailRequestTO requestTO) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mimeMessageHelper.setFrom(mailProperties.getSender());
            mimeMessageHelper.setTo(requestTO.getRecipients().toArray(new String[requestTO.getRecipients().size()]));
            mimeMessageHelper.setSubject(requestTO.getSubject());
            mimeMessageHelper.setText(requestTO.getText(), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException messagingException) {
            throw new MailException(messagingException);
        }
    }

}
