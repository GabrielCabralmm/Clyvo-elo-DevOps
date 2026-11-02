package com.clyvoelo.painel.dto;

import com.clyvoelo.painel.model.enums.PrioridadeAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RelatoAlertaForm {

    @NotNull(message = "Informe a urgência")
    private PrioridadeAlerta prioridade = PrioridadeAlerta.MEDIA;

    @NotBlank(message = "Conte o que está acontecendo com o pet")
    @Size(min = 10, max = 500, message = "Escreva entre 10 e 500 caracteres")
    private String descricao;

    public PrioridadeAlerta getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeAlerta prioridade) { this.prioridade = prioridade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
