package br.pro.fagnerlima.spring.auth.api.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BaseService<T> {

    public T findById(Long id);

    public Page<T> findAll(Pageable pageable);

    public Page<T> findAll(Specification<T> specification, Pageable pageable);

    public List<T> findAllActive();

    public T save(T entity);

    public T update(Long id, T entity);

    public T switchActive(Long id);

}
