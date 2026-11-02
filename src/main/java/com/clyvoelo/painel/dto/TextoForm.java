package com.clyvoelo.painel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TextoForm {

    @NotBlank(message = "Este campo é obrigatório")
    @Size(min = 10, max = 500, message = "Escreva entre 10 e 500 caracteres")
    private String texto;

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
