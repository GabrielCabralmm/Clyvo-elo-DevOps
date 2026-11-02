package com.clyvoelo.painel.model.enums;

public enum TipoEvento {
    VACINA("Vacina"),
    CHECKUP("Check-up"),
    POS_OPERATORIO("Pós-operatório"),
    MEDICACAO("Medicação"),
    EXAME("Exame");

    private final String descricao;

    TipoEvento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
