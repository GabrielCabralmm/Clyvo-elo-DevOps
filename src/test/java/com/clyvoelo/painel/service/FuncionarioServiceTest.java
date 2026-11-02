package com.clyvoelo.painel.service;

import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.model.Funcionario;
import com.clyvoelo.painel.model.enums.Perfil;
import com.clyvoelo.painel.repository.AlertaRepository;
import com.clyvoelo.painel.repository.EventoClinicoRepository;
import com.clyvoelo.painel.repository.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private EventoClinicoRepository eventoClinicoRepository;
    @Mock private AlertaRepository alertaRepository;
    @Mock private CredencialService credencialService;

    @InjectMocks private FuncionarioService funcionarioService;

    @Test
    void excluir_propriaConta_lancaRegraDeNegocio() {
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(funcionario(3L, Perfil.ADMIN)));

        assertThatThrownBy(() -> funcionarioService.excluir(3L, 3L)).isInstanceOf(RegraDeNegocioException.class);
        verify(funcionarioRepository, never()).delete(any());
    }

    @Test
    void excluir_veterinarioComAtendimentos_lancaRegraDeNegocio() {
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario(1L, Perfil.DOUTOR)));
        when(eventoClinicoRepository.existsByFuncionarioAtendeuId(1L)).thenReturn(true);

        assertThatThrownBy(() -> funcionarioService.excluir(1L, 3L)).hasMessageContaining("atendimentos");
    }

    @Test
    void buscarVeterinario_funcionarioAdministrador_lancaRegraDeNegocio() {
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(funcionario(3L, Perfil.ADMIN)));

        assertThatThrownBy(() -> funcionarioService.buscarVeterinario(3L)).isInstanceOf(RegraDeNegocioException.class);
    }

    private Funcionario funcionario(Long id, Perfil perfil) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(id);
        funcionario.setNome("Funcionário " + id);
        funcionario.setPerfil(perfil);
        return funcionario;
    }
}
