package br.pro.fagnerlima.spring.auth.api.application.facade;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFacade {

    public static <T> ResponseEntity<T> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    public static <T> ResponseEntity<T> created(T data, String locationURI) {
        return ResponseEntity.created(URI.create(locationURI)).body(data);
    }

    public static <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok(data);
    }

    public static <T> ResponseEntity<T> notFound() {
        return ResponseEntity.notFound().build();
    }

    public static ResponseEntity<?> noContent() {
        return ResponseEntity.noContent().build();
    }

}
