package br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo;

import java.io.Serializable;
import java.util.Set;

import org.springframework.hateoas.RepresentationModel;

import br.pro.fagnerlima.spring.auth.api.presentation.dto.permissao.PermissaoResponseTO;

public class GrupoResponseTO extends RepresentationModel<GrupoResponseTO> implements Serializable {

    private static final long serialVersionUID = 3279197969952912698L;

    private Long id;

    private String nome;

    private Boolean ativo;

    private Set<PermissaoResponseTO> permissoes;

    public GrupoResponseTO() {
        super();
    }

    public GrupoResponseTO(Long id, String nome, Boolean ativo, Set<PermissaoResponseTO> permissoes) {
        super();
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
        this.permissoes = permissoes;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public Set<PermissaoResponseTO> getPermissoes() {
        return permissoes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public void setPermissoes(Set<PermissaoResponseTO> permissoes) {
        this.permissoes = permissoes;
    }

    @Override
    public String toString() {
        return String.format("GrupoResponseTO [id=%s, nome=%s, ativo=%s, permissoes=%s]", id, nome, ativo, permissoes);
    }

}
