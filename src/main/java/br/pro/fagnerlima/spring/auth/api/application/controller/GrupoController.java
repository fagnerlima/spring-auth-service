package br.pro.fagnerlima.spring.auth.api.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ResponseService;
import br.pro.fagnerlima.spring.auth.api.presentation.assembler.GrupoAssembler;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoReducedResponseTO;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private ResponseService responseService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GRUPO_PESQUISAR') and #oauth2.hasScope('read')")
    @GetMapping
    public ResponseEntity<ResponseTO<Page<GrupoReducedResponseTO>>> findAll(Pageable pageable) {
        Page<Grupo> page = grupoService.findAll(pageable);

        return responseService.ok((new GrupoAssembler()).from(page));
    }

}
