package br.pro.fagnerlima.spring.auth.api.presentation.dto.grupo;

import java.io.Serializable;

public class GrupoMinResponseTO implements Serializable {

    private static final long serialVersionUID = 6680724796313031974L;

    private Long id;

    private String nome;

    public GrupoMinResponseTO() {
        super();
    }

    public GrupoMinResponseTO(Long id, String nome) {
        super();
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return String.format("GrupoMinResponseTO [id=%s, nome=%s]", id, nome);
    }

}
