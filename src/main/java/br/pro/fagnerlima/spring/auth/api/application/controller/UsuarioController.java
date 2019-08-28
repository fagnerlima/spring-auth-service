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

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ConverterService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ResponseService;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioFilterRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioMinResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioReducedResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioResponseTO;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SpecificationFactory<Usuario> specificationFactory;

    @Autowired
    private ConverterService converterService;

    @Autowired
    private ResponseService responseService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USUARIO_LISTAR') and #oauth2.hasScope('read')")
    @GetMapping
    public ResponseEntity<ResponseTO<Page<UsuarioReducedResponseTO>>> findAll(UsuarioFilterRequestTO filterRequestTO, Pageable pageable) {
        Specification<Usuario> specification = specificationFactory.create(filterRequestTO);
        Page<Usuario> page = usuarioService.findAll(specification, pageable);
        Page<UsuarioReducedResponseTO> responseTOPage = converterService.convert(page, UsuarioReducedResponseTO.class);

        return responseService.ok(responseTOPage);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USUARIO_BUSCAR') and #oauth2.hasScope('read')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> find(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        UsuarioResponseTO responseTO = converterService.convert(usuario, UsuarioResponseTO.class);

        return responseService.ok(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USUARIO_LISTAR') and #oauth2.hasScope('read')")
    @GetMapping("/ativos")
    public ResponseEntity<ResponseTO<List<UsuarioMinResponseTO>>> findAllActives() {
        List<Usuario> usuarios = usuarioService.findAllActives();
        List<UsuarioMinResponseTO> responseTOList = converterService.convert(usuarios, UsuarioMinResponseTO.class);

        return responseService.ok(responseTOList);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USUARIO_SALVAR') and #oauth2.hasScope('write')")
    @PostMapping
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> save(@RequestBody UsuarioRequestTO requestTO) {
        Usuario usuario = converterService.convert(requestTO, Usuario.class);
        Usuario savedUsuario = usuarioService.save(usuario);
        UsuarioResponseTO responseTO = converterService.convert(savedUsuario, UsuarioResponseTO.class);

        return responseService.created(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USUARIO_EDITAR') and #oauth2.hasScope('write')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> update(@PathVariable Long id, @RequestBody UsuarioRequestTO requestTO) {
        Usuario usuario = converterService.convert(requestTO, Usuario.class);
        Usuario updatedUsuario = usuarioService.update(id, usuario);
        UsuarioResponseTO responseTO = converterService.convert(updatedUsuario, UsuarioResponseTO.class);

        return responseService.ok(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USUARIO_ALTERAR_STATUS') and #oauth2.hasScope('write')")
    @PatchMapping("/{id}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void switchActive(@PathVariable Long id) {
        usuarioService.switchActive(id);
    }

}
