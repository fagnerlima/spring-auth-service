package br.pro.fagnerlima.spring.auth.api.application.factory;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;

import br.pro.fagnerlima.spring.auth.api.application.controller.UsuarioController;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.usuario.UsuarioFilterRequestTO;

public class UsuarioLinkFactory {

    public static List<Link> create(Long id) {
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(UsuarioController.class)
                .findById(id))
                .withSelfRel());
        links.add(linkTo(methodOn(UsuarioController.class)
                .findAll(new UsuarioFilterRequestTO(), PageRequest.of(0, 10)))
                .withRel("usuarios"));
        links.add(linkTo(methodOn(UsuarioController.class)
                .findAllActive())
                .withRel("ativos"));

        return links;
    }

}
