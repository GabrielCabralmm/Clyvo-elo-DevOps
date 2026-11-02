package com.clyvoelo.painel.repository;

import com.clyvoelo.painel.model.AcompanhamentoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcompanhamentoAlertaRepository extends JpaRepository<AcompanhamentoAlerta, Long> {
    List<AcompanhamentoAlerta> findByAlertaIdOrderByDataRegistroAsc(Long idAlerta);
}
