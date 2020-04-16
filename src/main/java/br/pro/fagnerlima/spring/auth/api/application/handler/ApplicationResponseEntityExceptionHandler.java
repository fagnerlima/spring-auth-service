package br.pro.fagnerlima.spring.auth.api.application.handler;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.BusinessException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.DuplicateKeyException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidActualPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidTokenException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.NotAuthenticatedUserException;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.exception.AuthenticationException;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.exception.IncorrectUsernameOrPasswordException;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MessageService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.exception.MailException;

@ControllerAdvice
public class ApplicationResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MessageService messageService;

    public ApplicationResponseEntityExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception, WebRequest request) {
        logger.error(exception.getMessage(), exception);

        return handleException(exception, HttpStatus.BAD_REQUEST, request, "resource.invalid-operation");
    }

    @ExceptionHandler({ BusinessException.class })
    public ResponseEntity<Object> handleBusinessException(BusinessException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, exception.getMessage());
    }

    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, exception.getMessage());
    }

    @ExceptionHandler({ NotAuthenticatedUserException.class })
    public ResponseEntity<Object> handleNotAuthenticatedUserException(NotAuthenticatedUserException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "security.not-authenticated");
    }

    @ExceptionHandler({ InformationNotFoundException.class })
    public ResponseEntity<Object> handleInformationNotFoundException(InformationNotFoundException exception, WebRequest request) {
        return handleException(exception, HttpStatus.NOT_FOUND, request, "resource.information-not-found");
    }

    @ExceptionHandler({ IncorrectUsernameOrPasswordException.class })
    public ResponseEntity<Object> handleUsernameNotFoundException(IncorrectUsernameOrPasswordException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "security.incorrect-username-or-password");
    }

    @ExceptionHandler({ InvalidPasswordException.class })
    public ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "security.invalid-password");
    }

    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "security.access-denied");
    }

    @ExceptionHandler({ InvalidActualPasswordException.class })
    public ResponseEntity<Object> handleInvalidActualPasswordException(InvalidActualPasswordException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "security.invalid-actual-password");
    }

    @ExceptionHandler({ InvalidTokenException.class })
    public ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "security.invalid-token");
    }

    @ExceptionHandler({ DuplicateKeyException.class })
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, exception.getMessage());
    }

    @ExceptionHandler({ MailException.class })
    public ResponseEntity<Object> handleMailException(MailException exception, WebRequest request) {
        logger.error(exception.getMessage(), exception);

        return handleException(exception, HttpStatus.BAD_REQUEST, request, "mail.error");
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "resource.invalid-operation");
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception, WebRequest request) {
        List<String> errors = exception.getConstraintViolations().stream()
                .map(e -> MessageFormat.format(e.getMessage(), e.getPropertyPath())).collect(Collectors.toList());

        return handleExceptionInternal(exception, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        List<String> errors = getErrors(exception.getBindingResult());

        return handleExceptionInternal(exception, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    protected ResponseEntity<Object> handleException(Exception exception, HttpStatus status, WebRequest request, String key) {
        List<String> errors = List.of(messageService.getMessage(key));

        return handleExceptionInternal(exception, errors, new HttpHeaders(), status, request);
    }

    protected List<String> getErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(e -> messageService.getMessage(e))
                .collect(Collectors.toList());
    }

}
