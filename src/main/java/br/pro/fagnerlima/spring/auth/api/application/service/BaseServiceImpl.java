package br.pro.fagnerlima.spring.auth.api.application.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.domain.service.BaseService;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;

public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    @Transactional(readOnly = true)
    @Override
    public T findById(Long id) {
        Optional<T> entityOpt = getRepository().findById(id);

        if (!entityOpt.isPresent()) {
            throw new InformationNotFoundException();
        }

        return entityOpt.get();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Transactional
    @Override
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    @Override
    public T update(Long id, T entity) {
        T entitySaved = findById(id);
        BeanUtils.copyProperties(entity, entitySaved, "id");

        return save(entitySaved);
    }

    protected abstract BaseRepository<T> getRepository();
}
