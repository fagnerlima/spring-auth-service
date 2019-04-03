package br.pro.fagnerlima.spring.auth.api.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.GrupoRepository;

@Service
public class GrupoServiceImpl extends BaseServiceImpl<Grupo> implements GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Override
    protected BaseRepository<Grupo> getRepository() {
        return grupoRepository;
    }

}
