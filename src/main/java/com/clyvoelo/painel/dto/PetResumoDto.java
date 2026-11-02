package com.clyvoelo.painel.dto;

import com.clyvoelo.painel.model.Alerta;
import com.clyvoelo.painel.model.EventoClinico;
import com.clyvoelo.painel.model.Pet;

public record PetResumoDto(Pet pet, EventoClinico proximoEvento, long alertasAtivos, Alerta ultimoAlerta) {
}
