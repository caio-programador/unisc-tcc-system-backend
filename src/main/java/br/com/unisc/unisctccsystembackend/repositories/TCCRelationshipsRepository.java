package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.TCCRelationships;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TCCRelationshipsRepository extends JpaRepository<TCCRelationships, Long> {
    Page<TCCRelationships> findByStudent_NameContainingIgnoreCase(String name, Pageable pageable);
    Optional<TCCRelationships> findByStudent_Id(Long id);
}
