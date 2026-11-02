package com.clyvoelo.painel.model;

import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.enums.OrigemAlerta;
import com.clyvoelo.painel.model.enums.PrioridadeAlerta;
import com.clyvoelo.painel.model.enums.StatusAlerta;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlertaTest {

    private final Pet pet = new Pet();
    private final Tutor tutor = new Tutor();
    private final Funcionario veterinario = new Funcionario();

    @Test
    void relatadoPeloTutor_novoRelato_ficaAbertoComOrigemTutor() {
        Alerta alerta = Alerta.relatadoPeloTutor(pet, tutor, PrioridadeAlerta.ALTA, "Está mancando");

        assertThat(alerta.getStatus()).isEqualTo(StatusAlerta.ABERTO);
        assertThat(alerta.getOrigem()).isEqualTo(OrigemAlerta.TUTOR);
        assertThat(alerta.getTutorRelatou()).isSameAs(tutor);
    }

    @Test
    void assumir_alertaAberto_passaParaEmAcompanhamento() {
        Alerta alerta = Alerta.relatadoPeloTutor(pet, tutor, PrioridadeAlerta.MEDIA, "Comendo pouco");

        alerta.assumir(veterinario);

        assertThat(alerta.getStatus()).isEqualTo(StatusAlerta.EM_ACOMPANHAMENTO);
        assertThat(alerta.getResponsavel()).isSameAs(veterinario);
    }

    @Test
    void resolver_semResponsavel_defineQuemResolveuComoResponsavel() {
        Alerta alerta = Alerta.relatadoPeloTutor(pet, tutor, PrioridadeAlerta.BAIXA, "Coceira");

        alerta.resolver(veterinario, "Alergia leve, orientado banho com shampoo neutro.");

        assertThat(alerta.estaResolvido()).isTrue();
        assertThat(alerta.getResponsavel()).isSameAs(veterinario);
        assertThat(alerta.getDataResolucao()).isEqualTo(LocalDate.now());
    }

    @Test
    void resolver_alertaJaResolvido_lancaRegraDeNegocio() {
        Alerta alerta = Alerta.relatadoPeloTutor(pet, tutor, PrioridadeAlerta.BAIXA, "Coceira");
        alerta.resolver(veterinario, "Resolvido.");

        assertThatThrownBy(() -> alerta.assumir(veterinario)).isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void diasEmAberto_alertaCriadoHaTresDias_contaTresDias() {
        Alerta alerta = Alerta.relatadoPeloTutor(pet, tutor, PrioridadeAlerta.ALTA, "Febre");
        alerta.setDataCriacao(LocalDate.now().minusDays(3));

        assertThat(alerta.getDiasEmAberto()).isEqualTo(3);
    }
}
