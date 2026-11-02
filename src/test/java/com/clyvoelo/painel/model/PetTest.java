package com.clyvoelo.painel.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetTest {

    @Test
    void idadeFormatada_nascidoHaDoisAnos_mostraAnos() {
        Pet pet = new Pet();
        pet.setDataNascimento(LocalDate.now().minusYears(2));

        assertThat(pet.getIdadeFormatada()).isEqualTo("2 anos");
    }

    @Test
    void idadeFormatada_filhoteDeCincoMeses_mostraMeses() {
        Pet pet = new Pet();
        pet.setDataNascimento(LocalDate.now().minusMonths(5));

        assertThat(pet.getIdadeFormatada()).isEqualTo("5 meses");
    }

    @Test
    void pesoFormatado_valorComCasasDecimais_usaVirgula() {
        Pet pet = new Pet();
        pet.setPesoKg(new BigDecimal("9.50"));

        assertThat(pet.getPesoFormatado()).isEqualTo("9,5 kg");
    }

    @Test
    void pertenceAo_tutorDiferente_retornaFalso() {
        Tutor dono = new Tutor();
        dono.setId(1L);
        Tutor outro = new Tutor();
        outro.setId(2L);
        Pet pet = new Pet();
        pet.setTutor(dono);

        assertThat(pet.pertenceAo(outro)).isFalse();
        assertThat(pet.pertenceAo(dono)).isTrue();
    }
}
