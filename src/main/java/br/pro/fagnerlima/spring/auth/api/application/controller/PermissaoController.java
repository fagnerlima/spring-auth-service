package br.pro.fagnerlima.spring.auth.api.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.PermissaoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ConverterService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ResponseService;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.permissao.PermissaoResponseTO;

@RestController
@RequestMapping("/permissoes")
public class PermissaoController {

    @Autowired
    private PermissaoService permissaoService;

    @Autowired
    private ConverterService converterService;

    @Autowired
    private ResponseService responseService;

    @GetMapping("/ativos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PERMISSAO_LISTAR') and #oauth2.hasScope('read')")
    public ResponseEntity<ResponseTO<List<PermissaoResponseTO>>> findAllActives() {
        List<Permissao> permissoes = permissaoService.findAllActives();
        List<PermissaoResponseTO> responseTOList = converterService.convert(permissoes, PermissaoResponseTO.class);

        return responseService.ok(responseTOList);
    }

}
