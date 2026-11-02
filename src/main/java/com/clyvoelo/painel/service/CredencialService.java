package com.clyvoelo.painel.service;

import com.clyvoelo.painel.exception.RegraDeNegocioException;
import com.clyvoelo.painel.repository.FuncionarioRepository;
import com.clyvoelo.painel.repository.TutorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CredencialService {

    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final FuncionarioRepository funcionarioRepository;
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    public CredencialService(FuncionarioRepository funcionarioRepository, TutorRepository tutorRepository,
                             PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.tutorRepository = tutorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String prepararEmailDeFuncionario(String email, Long idFuncionario) {
        String normalizado = normalizar(email);
        boolean emUso = (idFuncionario == null
                ? funcionarioRepository.existsByEmailIgnoreCase(normalizado)
                : funcionarioRepository.existsByEmailIgnoreCaseAndIdNot(normalizado, idFuncionario))
                || tutorRepository.existsByEmailIgnoreCase(normalizado);
        return garantirDisponivel(normalizado, emUso);
    }

    public String prepararEmailDeTutor(String email, Long idTutor) {
        String normalizado = normalizar(email);
        boolean emUso = (idTutor == null
                ? tutorRepository.existsByEmailIgnoreCase(normalizado)
                : tutorRepository.existsByEmailIgnoreCaseAndIdNot(normalizado, idTutor))
                || funcionarioRepository.existsByEmailIgnoreCase(normalizado);
        return garantirDisponivel(normalizado, emUso);
    }

    private String normalizar(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String garantirDisponivel(String email, boolean emUso) {
        if (emUso) {
            throw new RegraDeNegocioException("O email " + email + " já está em uso por outro usuário do painel.");
        }
        return email;
    }

    public String definirSenhaHash(String senhaInformada, String hashAtual) {
        boolean informouSenha = senhaInformada != null && !senhaInformada.isBlank();

        if (!informouSenha) {
            if (hashAtual == null) {
                throw new RegraDeNegocioException("Defina uma senha de acesso para o novo usuário.");
            }
            return hashAtual;
        }
        if (senhaInformada.length() < TAMANHO_MINIMO_SENHA) {
            throw new RegraDeNegocioException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        return passwordEncoder.encode(senhaInformada);
    }
}
