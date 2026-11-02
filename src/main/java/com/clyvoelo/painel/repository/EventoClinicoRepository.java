package com.clyvoelo.painel.repository;

import com.clyvoelo.painel.model.EventoClinico;
import com.clyvoelo.painel.model.enums.StatusEvento;
import com.clyvoelo.painel.model.enums.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoClinicoRepository extends JpaRepository<EventoClinico, Long> {

    @Query("""
            SELECT e FROM EventoClinico e
            WHERE (:status IS NULL OR e.status = :status)
              AND (:tipo IS NULL OR e.tipoEvento = :tipo)
              AND (:idPet IS NULL OR e.pet.id = :idPet)
              AND (:inicio IS NULL OR e.dataEvento >= :inicio)
              AND (:fim IS NULL OR e.dataEvento <= :fim)
            ORDER BY e.dataEvento
            """)
    List<EventoClinico> buscar(@Param("status") StatusEvento status,
                               @Param("tipo") TipoEvento tipo,
                               @Param("idPet") Long idPet,
                               @Param("inicio") LocalDate inicio,
                               @Param("fim") LocalDate fim);

    List<EventoClinico> findByStatusOrderByDataEventoAsc(StatusEvento status);

    List<EventoClinico> findByPetIdOrderByDataEventoDesc(Long idPet);

    List<EventoClinico> findTop5ByStatusOrderByDataAtendimentoDesc(StatusEvento status);

    long countByStatus(StatusEvento status);

    long countByTipoEvento(TipoEvento tipo);

    long countByStatusAndDataEventoBefore(StatusEvento status, LocalDate data);

    long countByFuncionarioAtendeuIdAndDataAtendimentoGreaterThanEqual(Long idFuncionario, LocalDate data);

    boolean existsByPetId(Long idPet);

    boolean existsByFuncionarioAtendeuId(Long idFuncionario);
}
