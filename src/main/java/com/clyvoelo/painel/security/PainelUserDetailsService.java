package com.clyvoelo.painel.security;

import com.clyvoelo.painel.model.enums.Perfil;
import com.clyvoelo.painel.repository.FuncionarioRepository;
import com.clyvoelo.painel.repository.TutorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PainelUserDetailsService implements UserDetailsService {

    private final FuncionarioRepository funcionarioRepository;
    private final TutorRepository tutorRepository;

    public PainelUserDetailsService(FuncionarioRepository funcionarioRepository, TutorRepository tutorRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.tutorRepository = tutorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String emailNormalizado = email.trim().toLowerCase();

        return funcionarioRepository.findByEmail(emailNormalizado)
                .map(f -> new UsuarioAutenticado(f.getId(), f.getNome(), f.getEmail(), f.getSenhaHash(), f.getPerfil()))
                .or(() -> tutorRepository.findByEmail(emailNormalizado)
                        .map(t -> new UsuarioAutenticado(t.getId(), t.getNome(), t.getEmail(), t.getSenhaHash(), Perfil.TUTOR)))
                .orElseThrow(() -> new UsernameNotFoundException("Email ou senha inválidos"));
    }
}
