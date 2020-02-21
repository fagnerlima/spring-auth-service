package br.pro.fagnerlima.spring.auth.api.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.PermissaoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.PermissaoRepository;

@Service
public class PermissaoServiceImpl extends BaseServiceImpl<Permissao> implements PermissaoService {

    @Autowired
    private PermissaoRepository permissaoRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("PermissaoServiceImpl_findAllActive")
    public List<Permissao> findAllActive() {
        return super.findAllActive();
    }

    @Override
    protected BaseRepository<Permissao> getRepository() {
        return permissaoRepository;
    }

}
