package com.clyvoelo.painel.model.enums;

public enum OrigemAlerta {
    SENSOR_IOT("Sensor IoT"),
    MANUAL("Registro manual"),
    TUTOR("Relato do tutor");

    private final String descricao;

    OrigemAlerta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
