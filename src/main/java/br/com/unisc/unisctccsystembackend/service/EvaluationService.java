package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.*;
import br.com.unisc.unisctccsystembackend.entities.DTO.*;
import br.com.unisc.unisctccsystembackend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private DeliverablesRepository deliverablesRepository;

    @Autowired
    private DeliverablesService deliverablesService;

    public List<EvaluationResponseDTO> getEvaluationsByDeliveryId(Long deliveryId) {
        List<Evaluation> evaluation = evaluationRepository.findAllByDelivery_Id(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found for this delivery"));
        return evaluation.stream().map(this::toDTO).toList();
    }

    public EvaluationResponseDTO createEvaluation(EvaluationRequestDTO dto, User professor) throws BadRequestException {
        Deliverables delivery = deliverablesRepository.findById(dto.deliveryId())
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        // regra: professor deve ser o orientador
        if (!delivery.getTcc().getProfessor().getId().equals(professor.getId())) {
            throw new BadRequestException("You are not allowed to evaluate this delivery");
        }

        if(dto.total() < 7)
            if (delivery.getDeliveryType().equals(DeliveryType.REELABORACAO_PROPOSTA))
                deliverablesService.updateDeliverableStatus(delivery.getId(), DeliveryStatus.REELABORACAO_REPROVADA);
            else
                deliverablesService.updateDeliverableStatus(delivery.getId(), DeliveryStatus.REPROVADO);
        else
            deliverablesService.updateDeliverableStatus(delivery.getId(), DeliveryStatus.APROVADO);

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
        if (dto.total() != null) {
            evaluation.setTotal(dto.total());
            if(dto.total() < 7)
                if (evaluation.getDelivery().getDeliveryType().equals(DeliveryType.REELABORACAO_PROPOSTA))
                    deliverablesService.updateDeliverableStatus(evaluation.getDelivery().getId(), DeliveryStatus.REELABORACAO_REPROVADA);
                else
                    deliverablesService.updateDeliverableStatus(evaluation.getDelivery().getId(), DeliveryStatus.REPROVADO);
            else
                deliverablesService.updateDeliverableStatus(evaluation.getDelivery().getId(), DeliveryStatus.APROVADO);
        }
        if (dto.comments() != null) evaluation.setComments(dto.comments());

        evaluation.setEvaluationDate(LocalDateTime.now());

        evaluationRepository.save(evaluation);
        return toDTO(evaluation);
    }

    public EvaluationResponseDTO getEvaluationByDeliveryIdAndProfessorId(Long deliveryId, Long professorId) {
        Evaluation evaluation = evaluationRepository.findByDelivery_IdAndProfessor_Id(deliveryId, professorId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found for this delivery and professor"));
        return toDTO(evaluation);
    }

    private EvaluationResponseDTO toDTO(Evaluation evaluation) {
        return new EvaluationResponseDTO(
                evaluation.getId(),
                new DeliveryResponseDTO(
                        evaluation.getDelivery().getId(),
                        new TCCRelationshipsResponseDTO(
                                evaluation.getDelivery().getTcc().getId(),
                                evaluation.getDelivery().getTcc().getTccTitle(),
                                evaluation.getDelivery().getTcc().getProposalDeliveryDate(),
                                evaluation.getDelivery().getTcc().getTccDeliveryDate(),
                                evaluation.getDelivery().getTcc().getProposalAssessmentDate(),
                                evaluation.getDelivery().getTcc().getTccAssessmentDate(),
                                new UserResponseDTO(
                                        evaluation.getDelivery().getTcc().getStudent().getId(),
                                        evaluation.getDelivery().getTcc().getStudent().getName(),
                                        evaluation.getDelivery().getTcc().getStudent().getEmail(),
                                        evaluation.getDelivery().getTcc().getStudent().getRole().name()
                                ),
                                new UserResponseDTO(
                                        evaluation.getDelivery().getTcc().getProfessor().getId(),
                                        evaluation.getDelivery().getTcc().getProfessor().getName(),
                                        evaluation.getDelivery().getTcc().getProfessor().getEmail(),
                                        evaluation.getDelivery().getTcc().getProfessor().getRole().name()
                                )
                        ),
                        evaluation.getDelivery().getDeliveryType(),
                        evaluation.getDelivery().getDeliveryStatus(),
                        evaluation.getDelivery().getBucketFileKey(),
                        evaluation.getDelivery().getDeliveryDate()
                ),
                new UserResponseDTO(
                        evaluation.getProfessor().getId(),
                        evaluation.getProfessor().getName(),
                        evaluation.getProfessor().getEmail(),
                        evaluation.getProfessor().getRole().name()
                ),
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
