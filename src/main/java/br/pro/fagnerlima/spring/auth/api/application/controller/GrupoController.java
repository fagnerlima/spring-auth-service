package br.pro.fagnerlima.spring.auth.api.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationBuilder;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ConverterService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ResponseService;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoFilterRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoMinResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoReducedResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoResponseTO;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private ConverterService converterService;

    @Autowired
    private ResponseService responseService;

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_LISTAR') and #oauth2.hasScope('read')")
    @GetMapping
    public ResponseEntity<ResponseTO<Page<GrupoReducedResponseTO>>> findAll(GrupoFilterRequestTO filterRequestTO, Pageable pageable) {
        Specification<Grupo> specification = new SpecificationBuilder<Grupo>()
                .and(filterRequestTO)
                .build();
        Page<Grupo> page = grupoService.findAll(specification, pageable);
        Page<GrupoReducedResponseTO> responseTOPage = converterService.convert(page, GrupoReducedResponseTO.class);

        return responseService.ok(responseTOPage);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_BUSCAR') and #oauth2.hasScope('read')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseTO<GrupoResponseTO>> find(@PathVariable Long id) {
        Grupo grupo = grupoService.findById(id);
        GrupoResponseTO responseTO = converterService.convert(grupo, GrupoResponseTO.class);

        return responseService.ok(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_LISTAR') and #oauth2.hasScope('read')")
    @GetMapping("/ativos")
    public ResponseEntity<ResponseTO<List<GrupoMinResponseTO>>> findAllActive() {
        List<Grupo> grupos = grupoService.findAllActive();
        List<GrupoMinResponseTO> responseTOList = converterService.convert(grupos, GrupoMinResponseTO.class);

        return responseService.ok(responseTOList);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_SALVAR') and #oauth2.hasScope('write')")
    @PostMapping
    public ResponseEntity<ResponseTO<GrupoResponseTO>> save(@RequestBody GrupoRequestTO requestTO) {
        Grupo grupo = converterService.convert(requestTO, Grupo.class);
        Grupo savedGrupo = grupoService.save(grupo);
        GrupoResponseTO responseTO = converterService.convert(savedGrupo, GrupoResponseTO.class);

        return responseService.created(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_EDITAR') and #oauth2.hasScope('write')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseTO<GrupoResponseTO>> update(@PathVariable Long id, @RequestBody GrupoRequestTO requestTO) {
        Grupo grupo = converterService.convert(requestTO, Grupo.class);
        Grupo updatedGrupo = grupoService.update(id, grupo);
        GrupoResponseTO responseTO = converterService.convert(updatedGrupo, GrupoResponseTO.class);

        return responseService.ok(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_GRUPO_ALTERAR_STATUS') and #oauth2.hasScope('write')")
    @PatchMapping("/{id}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void switchActive(@PathVariable Long id) {
        grupoService.switchActive(id);
    }

}
