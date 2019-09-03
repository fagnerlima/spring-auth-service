package br.pro.fagnerlima.spring.auth.api.infrastructure.service;

import javax.validation.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class MessageService {

    @Autowired
    protected MessageSource messageSource;

    public String getMessage(String key) {
        return getMessage(key, null);
    }
    
    public String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public String getMessage(FieldError fieldError) {
        return messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
    }
    
    public String getMessage(Path path) {
        return getMessage(path, null);
    }
    
    public String getMessage(Path path, Object[] args) {
        String message;
        try {
            message = messageSource.getMessage(path.toString(), args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            message = path.toString();
        }
        return message;
    }

}
