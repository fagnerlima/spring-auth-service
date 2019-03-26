package br.pro.fagnerlima.spring.auth.api.domain.model.usuario;

public enum Status {

    PENDENTE("Pendente"),
    ATIVO("Ativo"),
    INATIVO("Inativo"),
    BLOQUEADO("Bloqueado");

    private String label;

    private Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
