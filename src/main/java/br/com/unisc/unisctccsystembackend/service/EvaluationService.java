package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.*;
import br.com.unisc.unisctccsystembackend.entities.DTO.*;
import br.com.unisc.unisctccsystembackend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private DeliverablesRepository deliverablesRepository;

    public EvaluationResponseDTO getEvaluationByDeliveryId(Long deliveryId) {
        Evaluation evaluation = evaluationRepository.findByDelivery_Id(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found for this delivery"));

        return toDTO(evaluation);
    }

    public EvaluationResponseDTO createEvaluation(EvaluationRequestDTO dto, User professor) throws BadRequestException {
        Deliverables delivery = deliverablesRepository.findById(dto.deliveryId())
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        // regra: professor deve ser o orientador
        if (!delivery.getTcc().getProfessor().getId().equals(professor.getId())) {
            throw new BadRequestException("You are not allowed to evaluate this delivery");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setDelivery(delivery);
        evaluation.setProfessor(professor);
        evaluation.setIntroduction(dto.introduction());
        evaluation.setGoals(dto.goals());
        evaluation.setBibliographyRevision(dto.bibliographyRevision());
        evaluation.setMethodology(dto.methodology());
        evaluation.setTotal(dto.total());
        evaluation.setComments(dto.comments());
        evaluation.setEvaluationDate(LocalDateTime.now());

        evaluationRepository.save(evaluation);
        return toDTO(evaluation);
    }

    public EvaluationResponseDTO updateEvaluation(Long evaluationId, EvaluationRequestDTO dto, User professor) throws BadRequestException {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));

        // apenas o mesmo professor pode atualizar
        if (!evaluation.getProfessor().getId().equals(professor.getId())) {
            throw new BadRequestException("You are not allowed to update this evaluation");
        }

        if (dto.introduction() != null) evaluation.setIntroduction(dto.introduction());
        if (dto.goals() != null) evaluation.setGoals(dto.goals());
        if (dto.bibliographyRevision() != null) evaluation.setBibliographyRevision(dto.bibliographyRevision());
        if (dto.methodology() != null) evaluation.setMethodology(dto.methodology());
        if (dto.total() != null) evaluation.setTotal(dto.total());
        if (dto.comments() != null) evaluation.setComments(dto.comments());

        evaluation.setEvaluationDate(LocalDateTime.now());

        evaluationRepository.save(evaluation);
        return toDTO(evaluation);
    }

    private EvaluationResponseDTO toDTO(Evaluation evaluation) {
        return new EvaluationResponseDTO(
                evaluation.getId(),
                evaluation.getDelivery().getId(),
                evaluation.getProfessor().getId(),
                evaluation.getIntroduction(),
                evaluation.getGoals(),
                evaluation.getBibliographyRevision(),
                evaluation.getMethodology(),
                evaluation.getTotal(),
                evaluation.getComments(),
                evaluation.getEvaluationDate()
        );
    }
}
