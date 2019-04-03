package br.pro.fagnerlima.spring.auth.api.presentation.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo.GrupoReducedResponseTO;

public class GrupoAssembler {

    public Page<GrupoReducedResponseTO> from(Page<Grupo> page) {
        List<GrupoReducedResponseTO> content = page.getContent().stream()
                .map(grupo -> from(grupo)).collect(Collectors.toList());

        return new PageImpl<GrupoReducedResponseTO>(content, page.getPageable(), page.getTotalElements());
    }

    public GrupoReducedResponseTO from(Grupo grupo) {
        return new GrupoReducedResponseTO(grupo.getId(), grupo.getNome(), grupo.getAtivo());
    }
}
