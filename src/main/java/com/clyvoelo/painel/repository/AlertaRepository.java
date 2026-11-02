package com.clyvoelo.painel.repository;

import com.clyvoelo.painel.model.Alerta;
import com.clyvoelo.painel.model.enums.OrigemAlerta;
import com.clyvoelo.painel.model.enums.PrioridadeAlerta;
import com.clyvoelo.painel.model.enums.StatusAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    @Query("""
            SELECT a FROM Alerta a
            WHERE (:status IS NULL OR a.status = :status)
              AND (:prioridade IS NULL OR a.prioridade = :prioridade)
              AND (:origem IS NULL OR a.origem = :origem)
            ORDER BY a.dataCriacao DESC
            """)
    List<Alerta> buscar(@Param("status") StatusAlerta status,
                        @Param("prioridade") PrioridadeAlerta prioridade,
                        @Param("origem") OrigemAlerta origem);

    List<Alerta> findByStatusInOrderByDataCriacaoAsc(Collection<StatusAlerta> status);

    List<Alerta> findByPetIdOrderByDataCriacaoDesc(Long idPet);

    List<Alerta> findByResponsavelIdAndStatusOrderByDataCriacaoAsc(Long idFuncionario, StatusAlerta status);

    long countByStatus(StatusAlerta status);

    long countByStatusInAndPrioridade(Collection<StatusAlerta> status, PrioridadeAlerta prioridade);

    boolean existsByPetId(Long idPet);

    boolean existsByResponsavelIdOrFuncionarioResolveuId(Long idResponsavel, Long idResolveu);
}
