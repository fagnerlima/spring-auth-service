package br.pro.fagnerlima.spring.auth.api.application.controller;

import java.util.List;

import javax.validation.Valid;

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

import br.pro.fagnerlima.spring.auth.api.application.service.ResponseEntityFactory;
import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.facade.ModelMapperFacade;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.specification.SpecificationFactory;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioEmailRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioFilterRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioMinResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioReducedResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioSenhaResetTokenRequestTO;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ModelMapperFacade converterService;

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_USUARIO_LISTAR') and #oauth2.hasScope('read')")
    @GetMapping
    public ResponseEntity<ResponseTO<Page<UsuarioReducedResponseTO>>> findAll(UsuarioFilterRequestTO filterRequestTO, Pageable pageable) {
        Specification<Usuario> specification = new SpecificationFactory<Usuario>().create(filterRequestTO);
        Page<Usuario> page = usuarioService.findAll(specification, pageable);
        Page<UsuarioReducedResponseTO> responseTOPage = converterService.map(page, UsuarioReducedResponseTO.class);

        return ResponseEntityFactory.ok(responseTOPage);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_USUARIO_BUSCAR') and #oauth2.hasScope('read')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> find(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        UsuarioResponseTO responseTO = converterService.map(usuario, UsuarioResponseTO.class);

        return ResponseEntityFactory.ok(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_USUARIO_LISTAR') and #oauth2.hasScope('read')")
    @GetMapping("/ativos")
    public ResponseEntity<ResponseTO<List<UsuarioMinResponseTO>>> findAllActive() {
        List<Usuario> usuarios = usuarioService.findAllActive();
        List<UsuarioMinResponseTO> responseTOList = converterService.map(usuarios, UsuarioMinResponseTO.class);

        return ResponseEntityFactory.ok(responseTOList);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_USUARIO_SALVAR') and #oauth2.hasScope('write')")
    @PostMapping
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> save(@RequestBody UsuarioRequestTO requestTO) {
        Usuario usuario = converterService.map(requestTO, Usuario.class);
        Usuario savedUsuario = usuarioService.save(usuario);
        UsuarioResponseTO responseTO = converterService.map(savedUsuario, UsuarioResponseTO.class);

        return ResponseEntityFactory.created(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_USUARIO_EDITAR') and #oauth2.hasScope('write')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> update(@PathVariable Long id, @RequestBody UsuarioRequestTO requestTO) {
        Usuario usuario = converterService.map(requestTO, Usuario.class);
        Usuario updatedUsuario = usuarioService.update(id, usuario);
        UsuarioResponseTO responseTO = converterService.map(updatedUsuario, UsuarioResponseTO.class);

        return ResponseEntityFactory.ok(responseTO);
    }

    @PatchMapping("/senha")
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> updateSenhaByResetToken(@RequestBody UsuarioSenhaResetTokenRequestTO requestTO) {
        Usuario usuario = usuarioService.updateSenhaByResetToken(requestTO.getToken(), requestTO.getSenha());
        UsuarioResponseTO responseTO = converterService.map(usuario, UsuarioResponseTO.class);

        return ResponseEntityFactory.ok(responseTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_USUARIO_ALTERAR_STATUS') and #oauth2.hasScope('write')")
    @PatchMapping("/{id}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void switchActive(@PathVariable Long id) {
        usuarioService.switchActive(id);
    }

    @PostMapping("/recuperacao/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recoverLogin(@Valid @RequestBody UsuarioEmailRequestTO requestTO) {
        usuarioService.recoverLogin(requestTO.getEmail());
    }

    @PostMapping("/recuperacao/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recoverSenha(@Valid @RequestBody UsuarioEmailRequestTO requestTO) {
        usuarioService.recoverSenha(requestTO.getEmail());
    }

}
