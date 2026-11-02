package com.clyvoelo.painel.service;

import com.clyvoelo.painel.dto.RelatoAlertaForm;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.AcompanhamentoAlerta;
import com.clyvoelo.painel.model.Alerta;
import com.clyvoelo.painel.model.Funcionario;
import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.model.Tutor;
import com.clyvoelo.painel.model.enums.PrioridadeAlerta;
import com.clyvoelo.painel.model.enums.StatusAlerta;
import com.clyvoelo.painel.repository.AcompanhamentoAlertaRepository;
import com.clyvoelo.painel.repository.AlertaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock private AlertaRepository alertaRepository;
    @Mock private AcompanhamentoAlertaRepository acompanhamentoRepository;
    @Mock private FuncionarioService funcionarioService;
    @Mock private TutorService tutorService;
    @Mock private PetService petService;

    @InjectMocks private AlertaService alertaService;

    @Test
    void relatarPeloTutor_petDeOutroTutor_lancaRegraDeNegocio() {
        Tutor dono = tutor(1L);
        Tutor outro = tutor(2L);
        Pet pet = new Pet();
        pet.setTutor(dono);
        when(petService.buscar(10L)).thenReturn(pet);
        when(tutorService.buscar(2L)).thenReturn(outro);

        assertThatThrownBy(() -> alertaService.relatarPeloTutor(10L, 2L, relato()))
                .isInstanceOf(RegraDeNegocioException.class);
        verify(alertaRepository, never()).save(any());
    }

    @Test
    void relatarPeloTutor_petDoProprioTutor_salvaAlertaERegistraNaLinhaDoTempo() {
        Tutor dono = tutor(1L);
        Pet pet = new Pet();
        pet.setTutor(dono);
        when(petService.buscar(10L)).thenReturn(pet);
        when(tutorService.buscar(1L)).thenReturn(dono);
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Alerta alerta = alertaService.relatarPeloTutor(10L, 1L, relato());

        assertThat(alerta.getStatus()).isEqualTo(StatusAlerta.ABERTO);
        verify(acompanhamentoRepository).save(any(AcompanhamentoAlerta.class));
    }

    @Test
    void registrarAnotacao_alertaAindaAberto_veterinarioAssumeAutomaticamente() {
        Alerta alerta = Alerta.relatadoPeloTutor(new Pet(), tutor(1L), PrioridadeAlerta.MEDIA, "Comendo pouco");
        Funcionario veterinario = new Funcionario();
        veterinario.setNome("Ana Ferreira");
        when(alertaRepository.findById(5L)).thenReturn(Optional.of(alerta));
        when(funcionarioService.buscarVeterinario(3L)).thenReturn(veterinario);

        alertaService.registrarAnotacao(5L, 3L, "Liguei para o tutor e orientei a dieta.");

        assertThat(alerta.getStatus()).isEqualTo(StatusAlerta.EM_ACOMPANHAMENTO);
        assertThat(alerta.getResponsavel()).isSameAs(veterinario);
    }

    private Tutor tutor(Long id) {
        Tutor tutor = new Tutor();
        tutor.setId(id);
        tutor.setNome("Tutor " + id);
        return tutor;
    }

    private RelatoAlertaForm relato() {
        RelatoAlertaForm form = new RelatoAlertaForm();
        form.setPrioridade(PrioridadeAlerta.ALTA);
        form.setDescricao("Vomitou duas vezes hoje de manhã");
        return form;
    }
}
