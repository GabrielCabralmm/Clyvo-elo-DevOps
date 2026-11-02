package com.clyvoelo.painel.dto;

import com.clyvoelo.painel.model.Alerta;
import com.clyvoelo.painel.model.EventoClinico;

import java.util.List;

public record DashboardVeterinarioDto(
        List<EventoClinico> atrasados,
        List<EventoClinico> hoje,
        List<EventoClinico> proximos,
        List<Alerta> alertasSemResponsavel,
        List<Alerta> meusAlertas,
        long atendimentosNoMes) {
}
