package br.pro.fagnerlima.spring.auth.api.application.service;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ResponseService;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;

@Service
public class ResponseServiceImpl implements ResponseService {

    public <T> ResponseEntity<ResponseTO<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseTO<T>(data));
    }

    public <T> ResponseEntity<ResponseTO<T>> created(T data, String locationURI) {
        return ResponseEntity.created(URI.create(locationURI)).body(new ResponseTO<T>(data));
    }

    public <T> ResponseEntity<ResponseTO<T>> ok(T data) {
        return ResponseEntity.ok(new ResponseTO<T>(data));
    }

    public <T> ResponseEntity<T> notFound() {
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> noContent() {
        return ResponseEntity.noContent().build();
    }

}
