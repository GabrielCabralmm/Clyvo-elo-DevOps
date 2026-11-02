package com.clyvoelo.painel.model.enums;

public enum Especie {
    CACHORRO("Cachorro"),
    GATO("Gato");

    private final String descricao;

    Especie(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
