package com.clyvoelo.painel.dto;

import com.clyvoelo.painel.model.Alerta;
import com.clyvoelo.painel.model.EventoClinico;

import java.util.List;

public record DashboardAdminDto(
        long totalPets,
        long totalTutores,
        long totalVeterinarios,
        long eventosPendentes,
        long eventosAtrasados,
        long alertasAtivos,
        List<ContagemDto> petsPorEspecie,
        List<ContagemDto> eventosPorTipo,
        List<ContagemDto> alertasPorPrioridade,
        List<EventoClinico> ultimosAtendimentos,
        List<Alerta> alertasCriticos) {

    public long totalEventosPorTipo() {
        return eventosPorTipo.stream().mapToLong(ContagemDto::quantidade).sum();
    }

    public long totalAlertasAtivosPorPrioridade() {
        return alertasPorPrioridade.stream().mapToLong(ContagemDto::quantidade).sum();
    }
}
