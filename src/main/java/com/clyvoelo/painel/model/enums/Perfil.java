package com.clyvoelo.painel.model.enums;

public enum Perfil {
    ADMIN("Administrador"),
    DOUTOR("Veterinário"),
    TUTOR("Tutor");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
