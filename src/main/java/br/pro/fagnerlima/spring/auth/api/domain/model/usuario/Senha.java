package br.pro.fagnerlima.spring.auth.api.domain.model.usuario;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.bean.Nullable;

@Embeddable
public class Senha {

    @Nullable
    @Size(min = 6, max = 64)
    @Column(name = "valor_senha")
    private String valor;

    @Nullable
    @Column(name = "reset_token_senha")
    private String resetToken;

    @Column(name = "tentativas_erro_senha")
    private Integer tentativasErro;

    @Column(name = "data_atualizacao_senha")
    private LocalDateTime dataAtualizacao;

    public Senha() {
        super();
        tentativasErro = 0;
        dataAtualizacao = LocalDateTime.now();
    }

    public Senha(String valor, String resetToken) {
        this();
        this.valor = valor;
        this.resetToken = resetToken;
    }

    public Senha(String valor, String resetToken, Integer tentativasErro) {
        this();
        this.valor = valor;
        this.resetToken = resetToken;
        this.tentativasErro = tentativasErro;
    }

    public void clearResetToken() {
        resetToken = null;
    }

    public void generateResetToken() {
        resetToken = UUID.randomUUID().toString();
    }

    public void addTentativaErro() {
        tentativasErro++;
    }

    public void resetTentativasErro() {
        tentativasErro = 0;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Integer getTentativasErro() {
        return tentativasErro;
    }

    public void setTentativasErro(Integer tentativasErro) {
        this.tentativasErro = tentativasErro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataAtualizacao == null) ? 0 : dataAtualizacao.hashCode());
        result = prime * result + ((resetToken == null) ? 0 : resetToken.hashCode());
        result = prime * result + ((tentativasErro == null) ? 0 : tentativasErro.hashCode());
        result = prime * result + ((valor == null) ? 0 : valor.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Senha other = (Senha) obj;
        if (dataAtualizacao == null) {
            if (other.dataAtualizacao != null)
                return false;
        } else if (!dataAtualizacao.equals(other.dataAtualizacao))
            return false;
        if (resetToken == null) {
            if (other.resetToken != null)
                return false;
        } else if (!resetToken.equals(other.resetToken))
            return false;
        if (tentativasErro == null) {
            if (other.tentativasErro != null)
                return false;
        } else if (!tentativasErro.equals(other.tentativasErro))
            return false;
        if (valor == null) {
            if (other.valor != null)
                return false;
        } else if (!valor.equals(other.valor))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Senha [valor=%s, resetToken=%s, tentativasErro=%s, dataAtualizacao=%s]", valor, resetToken,
                tentativasErro, dataAtualizacao);
    }
}
