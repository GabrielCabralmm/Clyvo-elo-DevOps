package com.clyvoelo.painel.dto;

public record ContagemDto(String rotulo, long quantidade, String cor) {

    public int percentualDe(long total) {
        return total == 0 ? 0 : (int) Math.round(quantidade * 100.0 / total);
    }
}
