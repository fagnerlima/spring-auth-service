package br.pro.fagnerlima.spring.auth.api.presentation.dto.email;

import java.io.Serializable;
import java.util.List;

public class MailRequestTO implements Serializable {

    private static final long serialVersionUID = 4026100198976603742L;

    private List<String> recipients;

    private String subject;

    private String text;

    public MailRequestTO() {
        super();
    }

    public MailRequestTO(List<String> recipients, String subject, String text) {
        super();
        this.recipients = recipients;
        this.subject = subject;
        this.text = text;
    }

    public MailRequestTO(String recipient, String subject, String text) {
        super();
        this.recipients = List.of(recipient);
        this.subject = subject;
        this.text = text;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("MailRequestTO [recipients=%s, subject=%s, text=%s]", recipients, subject, text);
    }

}
