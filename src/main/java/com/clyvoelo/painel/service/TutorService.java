package com.clyvoelo.painel.service;

import com.clyvoelo.painel.exception.RecursoNaoEncontradoException;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.Tutor;
import com.clyvoelo.painel.repository.PetRepository;
import com.clyvoelo.painel.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final PetRepository petRepository;
    private final CredencialService credencialService;

    public TutorService(TutorRepository tutorRepository, PetRepository petRepository,
                        CredencialService credencialService) {
        this.tutorRepository = tutorRepository;
        this.petRepository = petRepository;
        this.credencialService = credencialService;
    }

    public List<Tutor> listar() {
        return tutorRepository.findAllByOrderByNomeAsc();
    }

    public Tutor buscar(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor não encontrado."));
    }

    public Map<Long, Long> quantidadeDePetsPorTutor() {
        return petRepository.findAll().stream()
                .collect(Collectors.groupingBy(pet -> pet.getTutor().getId(), Collectors.counting()));
    }

    @Transactional
    public Tutor salvar(Tutor tutor, String senha) {
        Tutor existente = tutor.getId() == null ? null : buscar(tutor.getId());

        tutor.setEmail(credencialService.prepararEmailDeTutor(tutor.getEmail(), tutor.getId()));
        tutor.setSenhaHash(credencialService.definirSenhaHash(senha, existente == null ? null : existente.getSenhaHash()));
        tutor.setDataCadastro(existente == null ? LocalDate.now() : existente.getDataCadastro());

        return tutorRepository.save(tutor);
    }

    @Transactional
    public void excluir(Long id) {
        Tutor tutor = buscar(id);
        if (petRepository.countByTutorId(id) > 0) {
            throw new RegraDeNegocioException("Não é possível excluir " + tutor.getNome()
                    + ": ainda existem pets vinculados a este tutor.");
        }
        tutorRepository.delete(tutor);
    }
}
