package com.clyvoelo.painel.service;

import com.clyvoelo.painel.dto.ContagemDto;
import com.clyvoelo.painel.dto.DashboardAdminDto;
import com.clyvoelo.painel.dto.DashboardVeterinarioDto;
import com.clyvoelo.painel.model.Alerta;
import com.clyvoelo.painel.model.EventoClinico;
import com.clyvoelo.painel.model.enums.Especie;
import com.clyvoelo.painel.model.enums.Perfil;
import com.clyvoelo.painel.model.enums.PrioridadeAlerta;
import com.clyvoelo.painel.model.enums.StatusAlerta;
import com.clyvoelo.painel.model.enums.StatusEvento;
import com.clyvoelo.painel.model.enums.TipoEvento;
import com.clyvoelo.painel.repository.AlertaRepository;
import com.clyvoelo.painel.repository.EventoClinicoRepository;
import com.clyvoelo.painel.repository.FuncionarioRepository;
import com.clyvoelo.painel.repository.PetRepository;
import com.clyvoelo.painel.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private static final List<StatusAlerta> STATUS_ATIVOS = List.of(StatusAlerta.ABERTO, StatusAlerta.EM_ACOMPANHAMENTO);
    private static final int DIAS_DE_PROXIMOS_EVENTOS = 7;
    private static final Map<PrioridadeAlerta, String> CORES_PRIORIDADE = Map.of(
            PrioridadeAlerta.ALTA, "perigo", PrioridadeAlerta.MEDIA, "atencao", PrioridadeAlerta.BAIXA, "sucesso");

    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EventoClinicoRepository eventoClinicoRepository;
    private final AlertaRepository alertaRepository;

    public DashboardService(PetRepository petRepository, TutorRepository tutorRepository,
                            FuncionarioRepository funcionarioRepository, EventoClinicoRepository eventoClinicoRepository,
                            AlertaRepository alertaRepository) {
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.eventoClinicoRepository = eventoClinicoRepository;
        this.alertaRepository = alertaRepository;
    }

    public DashboardAdminDto montarDashboardAdmin() {
        LocalDate hoje = LocalDate.now();

        List<ContagemDto> petsPorEspecie = Arrays.stream(Especie.values())
                .map(e -> new ContagemDto(e.getDescricao(), petRepository.countByEspecie(e), "primaria"))
                .toList();
        List<ContagemDto> eventosPorTipo = Arrays.stream(TipoEvento.values())
                .map(t -> new ContagemDto(t.getDescricao(), eventoClinicoRepository.countByTipoEvento(t), "primaria"))
                .toList();
        List<ContagemDto> alertasPorPrioridade = Arrays.stream(PrioridadeAlerta.values())
                .map(p -> new ContagemDto(p.getDescricao(), alertaRepository.countByStatusInAndPrioridade(STATUS_ATIVOS, p),
                        CORES_PRIORIDADE.get(p)))
                .toList();

        List<Alerta> alertasCriticos = alertaRepository.findByStatusInOrderByDataCriacaoAsc(STATUS_ATIVOS).stream()
                .filter(a -> a.getPrioridade() == PrioridadeAlerta.ALTA)
                .limit(5)
                .toList();

        return new DashboardAdminDto(
                petRepository.count(),
                tutorRepository.count(),
                funcionarioRepository.countByPerfil(Perfil.DOUTOR),
                eventoClinicoRepository.countByStatus(StatusEvento.PENDENTE),
                eventoClinicoRepository.countByStatusAndDataEventoBefore(StatusEvento.PENDENTE, hoje),
                STATUS_ATIVOS.stream().mapToLong(alertaRepository::countByStatus).sum(),
                petsPorEspecie,
                eventosPorTipo,
                alertasPorPrioridade,
                eventoClinicoRepository.findTop5ByStatusOrderByDataAtendimentoDesc(StatusEvento.CONCLUIDO),
                alertasCriticos);
    }

    public DashboardVeterinarioDto montarDashboardVeterinario(Long idVeterinario) {
        LocalDate hoje = LocalDate.now();
        List<EventoClinico> pendentes = eventoClinicoRepository.findByStatusOrderByDataEventoAsc(StatusEvento.PENDENTE);

        List<Alerta> semResponsavel = alertaRepository.findByStatusInOrderByDataCriacaoAsc(List.of(StatusAlerta.ABERTO));
        List<Alerta> meusAlertas = alertaRepository.findByResponsavelIdAndStatusOrderByDataCriacaoAsc(
                idVeterinario, StatusAlerta.EM_ACOMPANHAMENTO);

        return new DashboardVeterinarioDto(
                pendentes.stream().filter(EventoClinico::estaAtrasado).toList(),
                pendentes.stream().filter(EventoClinico::ehHoje).toList(),
                pendentes.stream()
                        .filter(e -> e.getDataEvento().isAfter(hoje)
                                && !e.getDataEvento().isAfter(hoje.plusDays(DIAS_DE_PROXIMOS_EVENTOS)))
                        .toList(),
                semResponsavel,
                meusAlertas,
                eventoClinicoRepository.countByFuncionarioAtendeuIdAndDataAtendimentoGreaterThanEqual(
                        idVeterinario, hoje.withDayOfMonth(1)));
    }
}
