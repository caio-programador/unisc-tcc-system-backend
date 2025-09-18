package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<List<Evaluation>> findAllByDelivery_Id(Long deliveryId);
    Optional<Evaluation> findByDelivery_IdAndProfessor_Id(Long deliveryId, Long professorId);
}
