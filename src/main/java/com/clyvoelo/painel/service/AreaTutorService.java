package com.clyvoelo.painel.service;

import com.clyvoelo.painel.dto.PetResumoDto;
import com.clyvoelo.painel.exception.RecursoNaoEncontradoException;
import com.clyvoelo.painel.model.Alerta;
import com.clyvoelo.painel.model.EventoClinico;
import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.model.Tutor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AreaTutorService {

    private final PetService petService;
    private final TutorService tutorService;
    private final EventoClinicoService eventoClinicoService;
    private final AlertaService alertaService;

    public AreaTutorService(PetService petService, TutorService tutorService,
                            EventoClinicoService eventoClinicoService, AlertaService alertaService) {
        this.petService = petService;
        this.tutorService = tutorService;
        this.eventoClinicoService = eventoClinicoService;
        this.alertaService = alertaService;
    }

    public List<PetResumoDto> resumoDosPets(Long idTutor) {
        return petService.listarDoTutor(idTutor).stream().map(this::resumir).toList();
    }

    public Pet petDoTutor(Long idPet, Long idTutor) {
        Pet pet = petService.buscar(idPet);
        Tutor tutor = tutorService.buscar(idTutor);
        if (!pet.pertenceAo(tutor)) {
            throw new RecursoNaoEncontradoException("Pet não encontrado entre os seus pets.");
        }
        return pet;
    }

    private PetResumoDto resumir(Pet pet) {
        List<EventoClinico> eventos = eventoClinicoService.historicoDoPet(pet.getId());
        List<Alerta> alertas = alertaService.alertasDoPet(pet.getId());

        EventoClinico proximo = eventos.stream()
                .filter(e -> !e.estaConcluido())
                .min(Comparator.comparing(EventoClinico::getDataEvento))
                .orElse(null);
        long alertasAtivos = alertas.stream().filter(a -> !a.estaResolvido()).count();

        return new PetResumoDto(pet, proximo, alertasAtivos, alertas.isEmpty() ? null : alertas.get(0));
    }
}
