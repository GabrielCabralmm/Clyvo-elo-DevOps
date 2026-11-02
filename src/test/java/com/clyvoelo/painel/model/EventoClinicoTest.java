package com.clyvoelo.painel.model;

import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.enums.StatusEvento;
import com.clyvoelo.painel.model.enums.TipoEvento;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventoClinicoTest {

    private final Pet pet = new Pet();
    private final Funcionario veterinario = new Funcionario();

    @Test
    void agendar_novoEvento_ficaPendente() {
        EventoClinico evento = EventoClinico.agendar(pet, TipoEvento.VACINA, "V10", LocalDate.now().plusDays(3));

        assertThat(evento.getStatus()).isEqualTo(StatusEvento.PENDENTE);
        assertThat(evento.estaAtrasado()).isFalse();
    }

    @Test
    void situacao_pendenteComDataVencida_apareceComoAtrasado() {
        EventoClinico evento = EventoClinico.agendar(pet, TipoEvento.EXAME, "Hemograma", LocalDate.now().minusDays(2));

        assertThat(evento.getSituacao()).isEqualTo(StatusEvento.ATRASADO);
    }

    @Test
    void registrarAtendimento_eventoPendente_concluiComVeterinarioEData() {
        EventoClinico evento = EventoClinico.agendar(pet, TipoEvento.CHECKUP, "Rotina", LocalDate.now());

        evento.registrarAtendimento(veterinario, "Pet saudável, retorno em 6 meses.");

        assertThat(evento.estaConcluido()).isTrue();
        assertThat(evento.getFuncionarioAtendeu()).isSameAs(veterinario);
        assertThat(evento.getDataAtendimento()).isEqualTo(LocalDate.now());
    }

    @Test
    void registrarAtendimento_eventoJaConcluido_lancaRegraDeNegocio() {
        EventoClinico evento = EventoClinico.agendar(pet, TipoEvento.CHECKUP, "Rotina", LocalDate.now());
        evento.registrarAtendimento(veterinario, "Primeiro atendimento.");

        assertThatThrownBy(() -> evento.registrarAtendimento(veterinario, "Segundo atendimento."))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void reagendar_eventoAtrasado_voltaParaPendenteNaNovaData() {
        EventoClinico evento = EventoClinico.agendar(pet, TipoEvento.MEDICACAO, "Antibiótico", LocalDate.now().minusDays(1));
        LocalDate novaData = LocalDate.now().plusDays(2);

        evento.reagendar(novaData);

        assertThat(evento.getDataEvento()).isEqualTo(novaData);
        assertThat(evento.getSituacao()).isEqualTo(StatusEvento.PENDENTE);
    }
}
