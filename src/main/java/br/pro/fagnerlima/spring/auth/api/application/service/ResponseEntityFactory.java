package br.pro.fagnerlima.spring.auth.api.application.service;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;

public class ResponseEntityFactory {

    public static <T> ResponseEntity<ResponseTO<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseTO<T>(data));
    }

    public static <T> ResponseEntity<ResponseTO<T>> created(T data, String locationURI) {
        return ResponseEntity.created(URI.create(locationURI)).body(new ResponseTO<T>(data));
    }

    public static <T> ResponseEntity<ResponseTO<T>> ok(T data) {
        return ResponseEntity.ok(new ResponseTO<T>(data));
    }

    public static <T> ResponseEntity<T> notFound() {
        return ResponseEntity.notFound().build();
    }

    public static ResponseEntity<?> noContent() {
        return ResponseEntity.noContent().build();
    }

}
