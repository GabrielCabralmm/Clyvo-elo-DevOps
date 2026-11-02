package com.clyvoelo.painel.service;

import com.clyvoelo.painel.dto.EventoFiltro;
import com.clyvoelo.painel.dto.NovoEventoForm;
import com.clyvoelo.painel.exception.RecursoNaoEncontradoException;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.EventoClinico;
import com.clyvoelo.painel.model.Funcionario;
import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.model.enums.StatusEvento;
import com.clyvoelo.painel.repository.EventoClinicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoClinicoService {

    private final EventoClinicoRepository eventoClinicoRepository;
    private final PetService petService;
    private final FuncionarioService funcionarioService;

    public EventoClinicoService(EventoClinicoRepository eventoClinicoRepository, PetService petService,
                                FuncionarioService funcionarioService) {
        this.eventoClinicoRepository = eventoClinicoRepository;
        this.petService = petService;
        this.funcionarioService = funcionarioService;
    }

    public List<EventoClinico> buscar(EventoFiltro filtro) {
        boolean somenteAtrasados = filtro.getStatus() == StatusEvento.ATRASADO;
        StatusEvento statusNoBanco = somenteAtrasados ? StatusEvento.PENDENTE : filtro.getStatus();

        List<EventoClinico> eventos = eventoClinicoRepository.buscar(statusNoBanco, filtro.getTipo(),
                filtro.getIdPet(), filtro.getInicio(), filtro.getFim());

        return somenteAtrasados ? eventos.stream().filter(EventoClinico::estaAtrasado).toList() : eventos;
    }

    public EventoClinico buscar(Long id) {
        return eventoClinicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento clínico não encontrado."));
    }

    public List<EventoClinico> historicoDoPet(Long idPet) {
        return eventoClinicoRepository.findByPetIdOrderByDataEventoDesc(idPet);
    }

    @Transactional
    public EventoClinico agendar(NovoEventoForm form) {
        Pet pet = petService.buscar(form.getIdPet());
        EventoClinico evento = EventoClinico.agendar(pet, form.getTipo(), form.getDescricao().trim(), form.getData());
        return eventoClinicoRepository.save(evento);
    }

    @Transactional
    public EventoClinico registrarAtendimento(Long idEvento, Long idVeterinario, String observacao) {
        EventoClinico evento = buscar(idEvento);
        Funcionario veterinario = funcionarioService.buscarVeterinario(idVeterinario);

        evento.registrarAtendimento(veterinario, observacao.trim());
        return eventoClinicoRepository.save(evento);
    }

    @Transactional
    public EventoClinico reagendar(Long idEvento, LocalDate novaData) {
        if (novaData == null || novaData.isBefore(LocalDate.now())) {
            throw new RegraDeNegocioException("Escolha uma nova data a partir de hoje.");
        }
        EventoClinico evento = buscar(idEvento);
        evento.reagendar(novaData);
        return eventoClinicoRepository.save(evento);
    }
}
