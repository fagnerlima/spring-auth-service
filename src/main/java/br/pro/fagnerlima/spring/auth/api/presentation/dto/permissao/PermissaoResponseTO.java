package br.pro.fagnerlima.spring.auth.api.presentation.dto.permissao;

import java.io.Serializable;

public class PermissaoResponseTO implements Serializable {

    private static final long serialVersionUID = 9134172781125306932L;

    private Long id;

    private String papel;

    private String descricao;

    public PermissaoResponseTO() {
        super();
    }

    public PermissaoResponseTO(Long id, String papel, String descricao) {
        super();
        this.id = id;
        this.papel = papel;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getPapel() {
        return papel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

	@Override
    public String toString() {
        return String.format("PermissaoResponseTO [id=%s, papel=%s, descricao=%s]", id, papel, descricao);
    }

}
