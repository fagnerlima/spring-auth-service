package br.pro.fagnerlima.spring.auth.api.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.pro.fagnerlima.spring.auth.api.domain.model.usuario.Usuario;
import br.pro.fagnerlima.spring.auth.api.domain.service.UsuarioService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ConverterService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.ResponseService;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.ResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioAutenticadoRequestTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioResponseTO;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioSenhaRequestTO;

@RestController
@RequestMapping("/me")
public class UsuarioAutenticadoController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ConverterService converterService;

    @Autowired
    private ResponseService responseService;

    @GetMapping
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> find() {
        Usuario usuario = usuarioService.getAutenticado();
        UsuarioResponseTO responseTO = converterService.convert(usuario, UsuarioResponseTO.class);

        return responseService.ok(responseTO);
    }

    @PutMapping
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> update(@RequestBody UsuarioAutenticadoRequestTO requestTO) {
        Usuario usuario = converterService.convert(requestTO, Usuario.class);
        Usuario updatedUsuario = usuarioService.updateAutenticado(usuario);
        UsuarioResponseTO responseTO = converterService.convert(updatedUsuario, UsuarioResponseTO.class);

        return responseService.ok(responseTO);
    }

    @PatchMapping("/senha")
    public ResponseEntity<ResponseTO<UsuarioResponseTO>> updateSenha(@RequestBody UsuarioSenhaRequestTO requestTO) {
        Usuario usuario = usuarioService.updateSenhaAutenticado(requestTO.getSenhaAtual(), requestTO.getSenhaNova());
        UsuarioResponseTO responseTO = converterService.convert(usuario, UsuarioResponseTO.class);

        return responseService.ok(responseTO);
    }

}
