package br.pro.fagnerlima.spring.auth.api.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.BaseService;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.BaseEntitySpecification;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.util.BeanUtils;

public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    @Autowired
    private OAuth2UserDetailsService userDetailsService;

    @Transactional(readOnly = true)
    @Override
    public T findById(Long id) {
        Optional<T> entityOpt = getRepository().findById(id);

        return entityOpt.orElseThrow(() -> new InformationNotFoundException());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<T> findAll(Pageable pageable) {
        if (getUsuarioAutenticado().isRoot()) {
            return getRepository().findAll(pageable);
        }

        return getRepository().findAll(BaseEntitySpecification.positiveId(), pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
        if (!getUsuarioAutenticado().isRoot()) {
            return getRepository().findAll(Specification
                    .where(specification)
                    .and(BaseEntitySpecification.positiveId()), pageable);
        }

        return getRepository().findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public List<T> findAllActive() {
        return getRepository().findAll(BaseEntitySpecification.positiveIdAndActive());
    }

    @Transactional
    @Override
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    @Override
    public T update(Long id, T entity) {
        T savedEntity = findById(id);
        BeanUtils.copyProperties(entity, savedEntity);

        return getRepository().save(savedEntity);
    }

    @Transactional
    @Override
    public T switchActive(Long id) {
        T entity = findById(id);
        entity.switchAtivo();

        return update(id, entity);
    }

    protected Usuario getUsuarioAutenticado() {
        return userDetailsService.getUsuarioAuth().getUsuario();
    }

    protected OAuth2UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    protected abstract BaseRepository<T> getRepository();

}
