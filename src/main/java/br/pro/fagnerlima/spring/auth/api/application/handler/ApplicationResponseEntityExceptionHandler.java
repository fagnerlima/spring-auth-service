package br.pro.fagnerlima.spring.auth.api.application.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.DuplicateKeyException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidActualPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidPasswordException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.InvalidTokenException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.NotAuthenticatedUserException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioBloqueadoException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioInativoException;
import br.pro.fagnerlima.spring.auth.api.application.service.exception.UsuarioPendenteException;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.MessageService;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;

@ControllerAdvice
public class ApplicationResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageService messageService;

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception, WebRequest request) {
        logger.error(exception.getMessage(), exception);

        return handleException(exception, HttpStatus.BAD_REQUEST, request, "resource.invalid-operation");
    }

    @ExceptionHandler({ NotAuthenticatedUserException.class })
    public ResponseEntity<Object> handleInvalidPermissionException(NotAuthenticatedUserException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "security.not-authenticated");
    }

    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    public ResponseEntity<Object> handleBadCredentialsException(RuntimeException exception, WebRequest request) {
        logger.error(exception.getMessage(), exception);

        return handleException(exception, HttpStatus.BAD_REQUEST, request, "resource.usuario.invalid-credentials");
    }

    @ExceptionHandler({ InformationNotFoundException.class })
    public ResponseEntity<Object> handleInformationNotFoundException(InformationNotFoundException exception, WebRequest request) {
        return handleException(exception, HttpStatus.NOT_FOUND, request, "resource.information-not-found");
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

    @ExceptionHandler({ UsuarioInativoException.class })
    public ResponseEntity<Object> handleUsuarioInativoException(UsuarioInativoException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "usuario.inativo");
    }

    @ExceptionHandler({ UsuarioPendenteException.class })
    public ResponseEntity<Object> handleUsuarioPendenteException(UsuarioPendenteException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "usuario.pendente");
    }

    @ExceptionHandler({ UsuarioBloqueadoException.class })
    public ResponseEntity<Object> handleUsuarioBloqueadoException(UsuarioBloqueadoException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "usuario.bloqueado");
    }

    @ExceptionHandler({ DuplicateKeyException.class })
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, exception.getMessage());
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException exception, WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request, "resource.invalid-operation");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        ResponseTO<List<String>> response = new ResponseTO<>();
        response.setErrors(getErrors(exception.getBindingResult()));

        return handleExceptionInternal(exception, response, headers, HttpStatus.BAD_REQUEST, request);
    }

    protected ResponseEntity<Object> handleException(Exception exception, HttpStatus status, WebRequest request, String key) {
        ResponseTO<List<String>> response = new ResponseTO<>();
        response.setErrors(Arrays.asList((messageService.getMessage(key))));

        return handleExceptionInternal(exception, response, new HttpHeaders(), status, request);
    }

    protected List<String> getErrors(BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(e -> errors.add(messageService.getMessage(e)));

        return errors;
    }

}