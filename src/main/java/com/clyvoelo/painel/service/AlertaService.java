package com.clyvoelo.painel.service;

import com.clyvoelo.painel.dto.AlertaFiltro;
import com.clyvoelo.painel.dto.RelatoAlertaForm;
import com.clyvoelo.painel.exception.RecursoNaoEncontradoException;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.AcompanhamentoAlerta;
import com.clyvoelo.painel.model.Alerta;
import com.clyvoelo.painel.model.Funcionario;
import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.model.Tutor;
import com.clyvoelo.painel.model.enums.StatusAlerta;
import com.clyvoelo.painel.repository.AcompanhamentoAlertaRepository;
import com.clyvoelo.painel.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final AcompanhamentoAlertaRepository acompanhamentoRepository;
    private final FuncionarioService funcionarioService;
    private final TutorService tutorService;
    private final PetService petService;

    public AlertaService(AlertaRepository alertaRepository, AcompanhamentoAlertaRepository acompanhamentoRepository,
                         FuncionarioService funcionarioService, TutorService tutorService, PetService petService) {
        this.alertaRepository = alertaRepository;
        this.acompanhamentoRepository = acompanhamentoRepository;
        this.funcionarioService = funcionarioService;
        this.tutorService = tutorService;
        this.petService = petService;
    }

    public List<Alerta> buscar(AlertaFiltro filtro) {
        List<Alerta> alertas = alertaRepository.buscar(filtro.getStatus(), filtro.getPrioridade(), filtro.getOrigem());
        if (filtro.isSomenteAtivos() && filtro.getStatus() == null) {
            return alertas.stream().filter(a -> !a.estaResolvido()).toList();
        }
        return alertas;
    }

    public Alerta buscar(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta não encontrado."));
    }

    public List<AcompanhamentoAlerta> acompanhamentos(Long idAlerta) {
        return acompanhamentoRepository.findByAlertaIdOrderByDataRegistroAsc(idAlerta);
    }

    public List<Alerta> alertasDoPet(Long idPet) {
        return alertaRepository.findByPetIdOrderByDataCriacaoDesc(idPet);
    }

    @Transactional
    public void assumir(Long idAlerta, Long idVeterinario) {
        Alerta alerta = buscar(idAlerta);
        Funcionario veterinario = funcionarioService.buscarVeterinario(idVeterinario);

        alerta.assumir(veterinario);
        alertaRepository.save(alerta);
        registrar(alerta, veterinario.getNome(), "Alerta assumido para acompanhamento.");
    }

    @Transactional
    public void registrarAnotacao(Long idAlerta, Long idVeterinario, String anotacao) {
        Alerta alerta = buscar(idAlerta);
        alerta.garantirQueNaoFoiResolvido();
        Funcionario veterinario = funcionarioService.buscarVeterinario(idVeterinario);

        if (alerta.getStatus() == StatusAlerta.ABERTO) {
            alerta.assumir(veterinario);
            alertaRepository.save(alerta);
        }
        registrar(alerta, veterinario.getNome(), anotacao.trim());
    }

    @Transactional
    public void resolver(Long idAlerta, Long idVeterinario, String nota) {
        Alerta alerta = buscar(idAlerta);
        Funcionario veterinario = funcionarioService.buscarVeterinario(idVeterinario);

        alerta.resolver(veterinario, nota.trim());
        alertaRepository.save(alerta);
        registrar(alerta, veterinario.getNome(), "Alerta resolvido: " + nota.trim());
    }

    @Transactional
    public Alerta relatarPeloTutor(Long idPet, Long idTutor, RelatoAlertaForm form) {
        Pet pet = petService.buscar(idPet);
        Tutor tutor = tutorService.buscar(idTutor);
        if (!pet.pertenceAo(tutor)) {
            throw new RegraDeNegocioException("Você só pode relatar alertas dos seus próprios pets.");
        }

        Alerta alerta = alertaRepository.save(
                Alerta.relatadoPeloTutor(pet, tutor, form.getPrioridade(), form.getDescricao().trim()));
        registrar(alerta, tutor.getNome(), "Relato enviado pelo tutor pelo painel.");
        return alerta;
    }

    private void registrar(Alerta alerta, String autor, String anotacao) {
        acompanhamentoRepository.save(AcompanhamentoAlerta.registrar(alerta, autor, anotacao));
    }
}
