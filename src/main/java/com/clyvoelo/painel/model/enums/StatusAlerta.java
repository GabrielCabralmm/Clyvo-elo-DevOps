package com.clyvoelo.painel.model.enums;

public enum StatusAlerta {
    ABERTO("Aberto"),
    EM_ACOMPANHAMENTO("Em acompanhamento"),
    RESOLVIDO("Resolvido");

    private final String descricao;

    StatusAlerta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
