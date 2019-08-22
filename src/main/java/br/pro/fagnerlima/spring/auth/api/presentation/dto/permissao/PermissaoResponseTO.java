package br.pro.fagnerlima.spring.auth.api.presentation.dto.permissao;

import java.io.Serializable;

public class PermissaoResponseTO implements Serializable {

    private static final long serialVersionUID = 9134172781125306932L;

    private Long id;

    private String papel;

    public PermissaoResponseTO() {
        super();
    }

    public PermissaoResponseTO(Long id, String papel) {
        super();
        this.id = id;
        this.papel = papel;
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

    @Override
    public String toString() {
        return String.format("PermissaoResponseTO [id=%s, papel=%s]", id, papel);
    }

}
