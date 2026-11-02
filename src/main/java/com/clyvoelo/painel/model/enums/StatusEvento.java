package com.clyvoelo.painel.model.enums;

public enum StatusEvento {
    PENDENTE("Pendente"),
    CONCLUIDO("Concluído"),
    ATRASADO("Atrasado");

    private final String descricao;

    StatusEvento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
