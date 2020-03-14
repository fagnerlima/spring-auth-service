package br.pro.fagnerlima.spring.auth.api.application.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.PermissaoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.PermissaoRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;

@Service
public class PermissaoServiceImpl extends BaseServiceImpl<Permissao> implements PermissaoService {

    private PermissaoRepository permissaoRepository;

    public PermissaoServiceImpl(OAuth2UserDetailsService userDetailsService, PermissaoRepository permissaoRepository) {
        super(userDetailsService);
        this.permissaoRepository = permissaoRepository;
    }

    @Override
    @Cacheable("PermissaoServiceImpl_findAllActive")
    public List<Permissao> findAllActive() {
        return super.findAllActive();
    }

    @Override
    protected BaseRepository<Permissao> getRepository() {
        return permissaoRepository;
    }

}
