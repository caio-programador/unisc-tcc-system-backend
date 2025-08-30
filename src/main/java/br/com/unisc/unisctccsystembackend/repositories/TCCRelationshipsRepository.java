package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.TCCRelationships;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TCCRelationshipsRepository extends JpaRepository<TCCRelationships, Long> {
    Page<TCCRelationships> findByStudent_NameContainingIgnoreCase(String name, Pageable pageable);
}
