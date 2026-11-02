package com.clyvoelo.painel.service;

import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.repository.FuncionarioRepository;
import com.clyvoelo.painel.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredencialServiceTest {

    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private TutorRepository tutorRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private CredencialService credencialService;

    @Test
    void definirSenhaHash_usuarioNovoSemSenha_lancaRegraDeNegocio() {
        assertThatThrownBy(() -> credencialService.definirSenhaHash("", null))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void definirSenhaHash_senhaCurta_lancaRegraDeNegocio() {
        assertThatThrownBy(() -> credencialService.definirSenhaHash("123", null))
                .hasMessageContaining("6 caracteres");
    }

    @Test
    void definirSenhaHash_edicaoSemNovaSenha_mantemHashAtual() {
        assertThat(credencialService.definirSenhaHash(" ", "hash-atual")).isEqualTo("hash-atual");
    }

    @Test
    void definirSenhaHash_senhaValida_gravaComBCrypt() {
        when(passwordEncoder.encode("clyvo123")).thenReturn("hash-novo");

        assertThat(credencialService.definirSenhaHash("clyvo123", "hash-atual")).isEqualTo("hash-novo");
    }

    @Test
    void prepararEmailDeTutor_emailUsadoPorFuncionario_lancaRegraDeNegocio() {
        when(tutorRepository.existsByEmailIgnoreCase("ana.ferreira@clyvovet.com")).thenReturn(false);
        when(funcionarioRepository.existsByEmailIgnoreCase("ana.ferreira@clyvovet.com")).thenReturn(true);

        assertThatThrownBy(() -> credencialService.prepararEmailDeTutor(" Ana.Ferreira@clyvovet.com ", null))
                .isInstanceOf(RegraDeNegocioException.class);
    }
}
