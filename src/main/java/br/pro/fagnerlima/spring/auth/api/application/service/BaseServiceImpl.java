package br.pro.fagnerlima.spring.auth.api.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.domain.service.BaseService;
import br.pro.fagnerlima.spring.auth.api.domain.shared.BaseEntity;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;

public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    @Autowired
    protected OAuth2UserDetailsService userDetailsService;

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
    public List<T> findAllActives() {
        return getRepository().findByAtivo(true);
    }

    @Transactional
    @Override
    public T save(T entity) {
        Long idUsuario = getIdUsuario();
        entity.setDataCriacao(LocalDateTime.now());
        entity.setIdUsuarioCriacao(idUsuario);
        entity.setDataAtualizacao(LocalDateTime.now());
        entity.setIdUsuarioAtualizacao(idUsuario);

        return getRepository().save(entity);
    }

    @Transactional
    @Override
    public T update(Long id, T entity) {
        T savedEntity = findById(id);
        BeanUtils.copyProperties(entity, savedEntity, "id");

        Long idUsuario = getIdUsuario();
        entity.setDataAtualizacao(LocalDateTime.now());
        entity.setIdUsuarioAtualizacao(idUsuario);

        return save(savedEntity);
    }

    protected Long getIdUsuario() {
        return userDetailsService.getUsuarioAuth().getUsuario().getId();
    }

    protected abstract BaseRepository<T> getRepository();
}
