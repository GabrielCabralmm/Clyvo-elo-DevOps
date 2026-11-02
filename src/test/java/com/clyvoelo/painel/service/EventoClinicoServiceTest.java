package com.clyvoelo.painel.service;

import com.clyvoelo.painel.dto.EventoFiltro;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.EventoClinico;
import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.model.enums.StatusEvento;
import com.clyvoelo.painel.model.enums.TipoEvento;
import com.clyvoelo.painel.repository.EventoClinicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventoClinicoServiceTest {

    @Mock private EventoClinicoRepository eventoClinicoRepository;
    @Mock private PetService petService;
    @Mock private FuncionarioService funcionarioService;

    @InjectMocks private EventoClinicoService eventoClinicoService;

    @Test
    void buscar_filtroAtrasado_retornaSomentePendentesComDataVencida() {
        EventoClinico atrasado = EventoClinico.agendar(new Pet(), TipoEvento.EXAME, "Exame", LocalDate.now().minusDays(2));
        EventoClinico futuro = EventoClinico.agendar(new Pet(), TipoEvento.VACINA, "Vacina", LocalDate.now().plusDays(2));
        when(eventoClinicoRepository.buscar(StatusEvento.PENDENTE, null, null, null, null)).thenReturn(List.of(atrasado, futuro));
        EventoFiltro filtro = new EventoFiltro();
        filtro.setStatus(StatusEvento.ATRASADO);

        List<EventoClinico> resultado = eventoClinicoService.buscar(filtro);

        assertThat(resultado).containsExactly(atrasado);
    }

    @Test
    void reagendar_dataNoPassado_lancaRegraDeNegocio() {
        assertThatThrownBy(() -> eventoClinicoService.reagendar(1L, LocalDate.now().minusDays(1)))
                .isInstanceOf(RegraDeNegocioException.class);
    }
}
