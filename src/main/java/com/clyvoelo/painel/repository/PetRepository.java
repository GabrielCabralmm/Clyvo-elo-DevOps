package com.clyvoelo.painel.repository;

import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.model.enums.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("""
            SELECT p FROM Pet p
            WHERE (LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(p.tutor.nome) LIKE LOWER(CONCAT('%', :busca, '%')))
              AND (:especie IS NULL OR p.especie = :especie)
            ORDER BY p.nome
            """)
    List<Pet> buscar(@Param("busca") String busca, @Param("especie") Especie especie);

    List<Pet> findByTutorIdOrderByNomeAsc(Long idTutor);

    long countByEspecie(Especie especie);

    long countByTutorId(Long idTutor);
}
