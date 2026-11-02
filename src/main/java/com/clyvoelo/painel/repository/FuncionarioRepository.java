package com.clyvoelo.painel.repository;

import com.clyvoelo.painel.model.Funcionario;
import com.clyvoelo.painel.model.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByEmail(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByEmailIgnoreCase(String email);
    List<Funcionario> findAllByOrderByNomeAsc();
    long countByPerfil(Perfil perfil);
}
