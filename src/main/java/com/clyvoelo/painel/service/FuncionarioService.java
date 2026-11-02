package com.clyvoelo.painel.service;

import com.clyvoelo.painel.exception.RecursoNaoEncontradoException;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.Funcionario;
import com.clyvoelo.painel.repository.AlertaRepository;
import com.clyvoelo.painel.repository.EventoClinicoRepository;
import com.clyvoelo.painel.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final EventoClinicoRepository eventoClinicoRepository;
    private final AlertaRepository alertaRepository;
    private final CredencialService credencialService;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, EventoClinicoRepository eventoClinicoRepository,
                              AlertaRepository alertaRepository, CredencialService credencialService) {
        this.funcionarioRepository = funcionarioRepository;
        this.eventoClinicoRepository = eventoClinicoRepository;
        this.alertaRepository = alertaRepository;
        this.credencialService = credencialService;
    }

    public List<Funcionario> listar() {
        return funcionarioRepository.findAllByOrderByNomeAsc();
    }

    public Funcionario buscar(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado."));
    }

    public Funcionario buscarVeterinario(Long id) {
        Funcionario funcionario = buscar(id);
        if (!funcionario.ehVeterinario()) {
            throw new RegraDeNegocioException("Apenas veterinários podem realizar esta ação.");
        }
        return funcionario;
    }

    @Transactional
    public Funcionario salvar(Funcionario funcionario, String senha) {
        Funcionario existente = funcionario.getId() == null ? null : buscar(funcionario.getId());

        funcionario.setEmail(credencialService.prepararEmailDeFuncionario(funcionario.getEmail(), funcionario.getId()));
        funcionario.setSenhaHash(credencialService.definirSenhaHash(senha, existente == null ? null : existente.getSenhaHash()));
        funcionario.setDataCadastro(existente == null ? LocalDate.now() : existente.getDataCadastro());

        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public void excluir(Long id, Long idUsuarioLogado) {
        Funcionario funcionario = buscar(id);
        if (funcionario.getId().equals(idUsuarioLogado)) {
            throw new RegraDeNegocioException("Você não pode excluir a sua própria conta.");
        }
        if (eventoClinicoRepository.existsByFuncionarioAtendeuId(id)
                || alertaRepository.existsByResponsavelIdOrFuncionarioResolveuId(id, id)) {
            throw new RegraDeNegocioException("Não é possível excluir " + funcionario.getNome()
                    + ": há atendimentos e alertas registrados em nome deste profissional.");
        }
        funcionarioRepository.delete(funcionario);
    }
}
