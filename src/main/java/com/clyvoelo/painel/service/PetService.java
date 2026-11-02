package com.clyvoelo.painel.service;

import com.clyvoelo.painel.exception.RecursoNaoEncontradoException;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.model.enums.Especie;
import com.clyvoelo.painel.repository.AlertaRepository;
import com.clyvoelo.painel.repository.EventoClinicoRepository;
import com.clyvoelo.painel.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final EventoClinicoRepository eventoClinicoRepository;
    private final AlertaRepository alertaRepository;
    private final TutorService tutorService;

    public PetService(PetRepository petRepository, EventoClinicoRepository eventoClinicoRepository,
                      AlertaRepository alertaRepository, TutorService tutorService) {
        this.petRepository = petRepository;
        this.eventoClinicoRepository = eventoClinicoRepository;
        this.alertaRepository = alertaRepository;
        this.tutorService = tutorService;
    }

    public List<Pet> listar() {
        return petRepository.buscar("", null);
    }

    public List<Pet> buscar(String busca, Especie especie) {
        return petRepository.buscar(busca == null ? "" : busca.trim(), especie);
    }

    public Pet buscar(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado."));
    }

    public List<Pet> listarDoTutor(Long idTutor) {
        return petRepository.findByTutorIdOrderByNomeAsc(idTutor);
    }

    @Transactional
    public Pet salvar(Pet pet) {
        pet.setTutor(tutorService.buscar(pet.getTutor().getId()));
        return petRepository.save(pet);
    }

    @Transactional
    public void excluir(Long id) {
        Pet pet = buscar(id);
        if (eventoClinicoRepository.existsByPetId(id) || alertaRepository.existsByPetId(id)) {
            throw new RegraDeNegocioException("Não é possível excluir " + pet.getNome()
                    + ": o pet já possui histórico de eventos clínicos ou alertas.");
        }
        petRepository.delete(pet);
    }
}
