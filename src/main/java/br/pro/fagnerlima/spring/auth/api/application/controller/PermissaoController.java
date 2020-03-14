package br.pro.fagnerlima.spring.auth.api.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.pro.fagnerlima.spring.auth.api.application.service.ResponseEntityFactory;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.PermissaoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.facade.ModelMapperFacade;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.permissao.PermissaoResponseTO;

@RestController
@RequestMapping("/permissoes")
public class PermissaoController {

    private PermissaoService permissaoService;

    private ModelMapperFacade converterService;

    public PermissaoController(PermissaoService permissaoService, ModelMapperFacade converterService) {
        this.permissaoService = permissaoService;
        this.converterService = converterService;
    }

    @GetMapping("/ativos")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_PERMISSAO_LISTAR') and #oauth2.hasScope('read')")
    public ResponseEntity<ResponseTO<List<PermissaoResponseTO>>> findAllActive() {
        List<Permissao> permissoes = permissaoService.findAllActive();
        List<PermissaoResponseTO> responseTOList = converterService.map(permissoes, PermissaoResponseTO.class);

        return ResponseEntityFactory.ok(responseTOList);
    }

}
