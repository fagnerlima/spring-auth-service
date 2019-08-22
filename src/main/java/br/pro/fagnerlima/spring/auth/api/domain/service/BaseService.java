package br.pro.fagnerlima.spring.auth.api.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T> {

    public T findById(Long id);

    public Page<T> findAll(Pageable pageable);

    public List<T> findAllActives();

    public T save(T entidade);

    public T update(Long id, T entidade);
}
