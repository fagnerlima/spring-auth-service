package br.pro.fagnerlima.spring.auth.api.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
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
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;

public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    @Autowired
    private OAuth2UserDetailsService userDetailsService;

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

    @Transactional(readOnly = true)
    @Override
    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
        return getRepository().findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public List<T> findAllActives() {
        return getRepository().findByAtivo(true);
    }

    @Transactional
    @Override
    public T save(T entity) {
        entity = auditSave(entity);

        return getRepository().save(entity);
    }

    @Transactional
    @Override
    public T update(Long id, T entity) {
        T savedEntity = findById(id);
        BeanUtils.copyProperties(entity, savedEntity, ignoredProperties());
        savedEntity = auditUpdate(savedEntity);

        return getRepository().save(savedEntity);
    }

    @Transactional
    @Override
    public T switchActive(Long id) {
        T entity = findById(id);
        entity.switchAtivo();

        return update(id, entity);
    }

    protected String[] ignoredProperties() {
        String[] ignoredProperties = { "id", "dataCriacao", "idUsuarioCriacao", "dataAtualizacao", "idUsuarioAtualizacao" };

        return ignoredProperties;
    }

    protected Usuario getUsuarioAutenticacado() {
        return userDetailsService.getUsuarioAuth().getUsuario();
    }

    protected T auditSave(T entity) {
        Long idUsuario = getUsuarioAutenticacado().getId();
        entity.setDataCriacao(LocalDateTime.now());
        entity.setIdUsuarioCriacao(idUsuario);
        entity.setDataAtualizacao(LocalDateTime.now());
        entity.setIdUsuarioAtualizacao(idUsuario);

        return entity;
    }

    protected T auditUpdate(T entity) {
        Long idUsuario = getUsuarioAutenticacado().getId();
        entity.setDataAtualizacao(LocalDateTime.now());
        entity.setIdUsuarioAtualizacao(idUsuario);

        return entity;
    }

    protected OAuth2UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    protected abstract BaseRepository<T> getRepository();

}
