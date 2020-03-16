package br.pro.fagnerlima.spring.auth.api.application.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.pro.fagnerlima.spring.auth.api.application.facade.ResponseEntityFacade;
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.facade.ModelMapperFacade;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoFilterRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoMinResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoReducedResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.factory.GrupoLinkFactory;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    private GrupoService grupoService;

    private ModelMapperFacade modelMapperFacade;

    public GrupoController(GrupoService grupoService, ModelMapperFacade modelMapperFacade) {
        this.grupoService = grupoService;
        this.modelMapperFacade = modelMapperFacade;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_LISTAR') and #oauth2.hasScope('read')")
    @GetMapping
    public ResponseEntity<Page<GrupoReducedResponseTO>> findAll(GrupoFilterRequestTO filterRequestTO, Pageable pageable) {
        Specification<Grupo> specification = new SpecificationFactory<Grupo>().create(filterRequestTO, Grupo.class);
        Page<Grupo> page = grupoService.findAll(specification, pageable);
        Page<GrupoReducedResponseTO> responseTOPage = modelMapperFacade.map(page, GrupoReducedResponseTO.class);

        responseTOPage.getContent().stream().forEach(responseTO -> responseTO.add(GrupoLinkFactory.create(responseTO.getId())));

        return ResponseEntityFacade.ok(responseTOPage);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_BUSCAR') and #oauth2.hasScope('read')")
    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseTO> findById(@PathVariable Long id) {
        Grupo grupo = grupoService.findById(id);
        GrupoResponseTO responseTO = modelMapperFacade.map(grupo, GrupoResponseTO.class);

        responseTO.add(GrupoLinkFactory.create(responseTO.getId()));

        return ResponseEntityFacade.ok(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_LISTAR') and #oauth2.hasScope('read')")
    @GetMapping("/ativos")
    public ResponseEntity<List<GrupoMinResponseTO>> findAllActive() {
        List<Grupo> grupos = grupoService.findAllActive();
        List<GrupoMinResponseTO> responseTOList = modelMapperFacade.map(grupos, GrupoMinResponseTO.class);

        responseTOList.stream().forEach(responseTO -> responseTO.add(GrupoLinkFactory.create(responseTO.getId())));

        return ResponseEntityFacade.ok(responseTOList);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_SALVAR') and #oauth2.hasScope('write')")
    @PostMapping
    public ResponseEntity<GrupoResponseTO> save(@RequestBody GrupoRequestTO requestTO) {
        Grupo grupo = modelMapperFacade.map(requestTO, Grupo.class);
        Grupo savedGrupo = grupoService.save(grupo);
        GrupoResponseTO responseTO = modelMapperFacade.map(savedGrupo, GrupoResponseTO.class);

        responseTO.add(GrupoLinkFactory.create(responseTO.getId()));

        return ResponseEntityFacade.created(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_EDITAR') and #oauth2.hasScope('write')")
    @PutMapping("/{id}")
    public ResponseEntity<GrupoResponseTO> update(@PathVariable Long id, @RequestBody GrupoRequestTO requestTO) {
        Grupo grupo = modelMapperFacade.map(requestTO, Grupo.class);
        Grupo updatedGrupo = grupoService.update(id, grupo);
        GrupoResponseTO responseTO = modelMapperFacade.map(updatedGrupo, GrupoResponseTO.class);

        responseTO.add(GrupoLinkFactory.create(responseTO.getId()));

        return ResponseEntityFacade.ok(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_ALTERAR_STATUS') and #oauth2.hasScope('write')")
    @PatchMapping("/{id}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void switchActive(@PathVariable Long id) {
        grupoService.switchActive(id);
    }

}
