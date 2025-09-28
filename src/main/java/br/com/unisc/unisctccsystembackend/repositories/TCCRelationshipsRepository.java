package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.TCCRelationships;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TCCRelationshipsRepository extends JpaRepository<TCCRelationships, Long> {
    Page<TCCRelationships> findByStudent_NameContainingIgnoreCase(String name, Pageable pageable);
    Optional<TCCRelationships> findByStudent_Id(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM tcc_relationships t WHERE t.id = :id")
    void deleteByIdDirectly(@Param("id") Long id);
}
