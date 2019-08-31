package br.pro.fagnerlima.spring.auth.api.domain.model.usuario;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class Senha {

    @Size(min = 6, max = 64)
    @Column(name = "valor_senha")
    private String valor;

    @Column(name = "reset_token_senha")
    private String resetToken;

    @Column(name = "tentativas_erro_senha")
    private Integer tentativasErro;

    @Column(name = "data_ultima_alteracao_senha")
    private LocalDateTime dataUltimaAlteracao;

    public Senha() {
        super();
        tentativasErro = 0;
    }

    public Senha(String valor, String resetToken, Integer tentativasErro, LocalDateTime dataUltimaAlteracao) {
        super();
        this.valor = valor;
        this.resetToken = resetToken;
        this.tentativasErro = tentativasErro;
        this.dataUltimaAlteracao = dataUltimaAlteracao;
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

    public Integer getTentativasErro() {
        return tentativasErro;
    }

    public void setTentativasErro(Integer tentativasErro) {
        this.tentativasErro = tentativasErro;
    }

    public LocalDateTime getDataUltimaAlteracao() {
        return dataUltimaAlteracao;
    }

    public void setDataUltimaAlteracao(LocalDateTime dataUltimaAlteracao) {
        this.dataUltimaAlteracao = dataUltimaAlteracao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataUltimaAlteracao == null) ? 0 : dataUltimaAlteracao.hashCode());
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
        if (dataUltimaAlteracao == null) {
            if (other.dataUltimaAlteracao != null)
                return false;
        } else if (!dataUltimaAlteracao.equals(other.dataUltimaAlteracao))
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
        return String.format("Senha [valor=%s, resetToken=%s, tentativasErro=%s, dataUltimaAlteracao=%s]", valor, resetToken,
                tentativasErro, dataUltimaAlteracao);
    }
}
