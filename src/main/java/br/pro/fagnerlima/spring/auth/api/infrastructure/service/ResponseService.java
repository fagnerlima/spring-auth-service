package br.pro.fagnerlima.spring.auth.api.infrastructure.service;

import org.springframework.http.ResponseEntity;

import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;

public interface ResponseService {

    public <T> ResponseEntity<ResponseTO<T>> created(T data);

    public <T> ResponseEntity<ResponseTO<T>> created(T data, String locationURI);

    public <T> ResponseEntity<ResponseTO<T>> ok(T data);

    public <T> ResponseEntity<T> notFound();

    public ResponseEntity<?> noContent();

}
