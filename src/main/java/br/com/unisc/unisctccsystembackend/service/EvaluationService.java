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

    @Autowired
    private AlertService alertService;

    public List<EvaluationResponseDTO> getEvaluationsByDeliveryId(Long deliveryId) {
        List<Evaluation> evaluation = evaluationRepository.findAllByDelivery_Id(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found for this delivery"));
        return evaluation.stream().map(this::toDTO).toList();
    }

    public EvaluationResponseDTO createEvaluation(EvaluationRequestDTO dto, User professor) throws BadRequestException {
        Deliverables delivery = deliverablesRepository.findById(dto.deliveryId())
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        verifyProfessorPermission(delivery, professor);

        double thisEvaluationTotal = dto.goals() + dto.introduction() + dto.bibliographyRevision() + dto.methodology();

        delivery.setTotalScore(delivery.getTotalScore() + thisEvaluationTotal);
        delivery.setAverageScore(delivery.getTotalScore() / 3);
        delivery.setQuantityEvaluations(delivery.getQuantityEvaluations() + 1);

        if(delivery.getQuantityEvaluations() == 3){
            if (delivery.getAverageScore() < 7)
                if (delivery.getDeliveryType().equals(DeliveryType.REELABORACAO_PROPOSTA))
                    deliverablesService.updateDeliverableStatus(delivery.getId(), DeliveryStatus.REELABORACAO_REPROVADA);
                else
                    deliverablesService.updateDeliverableStatus(delivery.getId(), DeliveryStatus.REPROVADO);
            else
                deliverablesService.updateDeliverableStatus(delivery.getId(), DeliveryStatus.APROVADO);
        }
        deliverablesRepository.save(delivery);

        Evaluation evaluation = new Evaluation();
        evaluation.setDelivery(delivery);
        evaluation.setProfessor(professor);
        evaluation.setIntroduction(dto.introduction());
        evaluation.setGoals(dto.goals());
        evaluation.setBibliographyRevision(dto.bibliographyRevision());
        evaluation.setMethodology(dto.methodology());
        evaluation.setTotal(thisEvaluationTotal);
        evaluation.setComments(dto.comments());
        evaluation.setEvaluationDate(LocalDateTime.now());

        evaluationRepository.save(evaluation);
        alertService.createOrUpdateAlert(
                evaluation.getDelivery().getTcc().getStudent(),
                "Sua entrega foi avaliada pelo professor " + professor.getName() + ". Verifique os detalhes da avaliação no sistema.",
                LocalDateTime.now(),
                AlertType.AVALIACAO_DISPONIVEL,
                null
        );
        return toDTO(evaluation);
    }

    public EvaluationResponseDTO updateEvaluation(Long evaluationId, EvaluationRequestDTO dto, User professor) throws BadRequestException {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));

        verifyProfessorPermission(evaluation.getDelivery(), professor);

        if (dto.introduction() != null) evaluation.setIntroduction(dto.introduction());
        if (dto.goals() != null) evaluation.setGoals(dto.goals());
        if (dto.bibliographyRevision() != null) evaluation.setBibliographyRevision(dto.bibliographyRevision());
        if (dto.methodology() != null) evaluation.setMethodology(dto.methodology());

        double thisEvaluationTotal = evaluation.getIntroduction() + evaluation.getGoals() + evaluation.getBibliographyRevision() + evaluation.getMethodology();
        evaluation.setTotal(thisEvaluationTotal);
        evaluation.getDelivery().setTotalScore(evaluation.getDelivery().getTotalScore() + thisEvaluationTotal);
        evaluation.getDelivery().setAverageScore(evaluation.getDelivery().getTotalScore() / 3);

        if(evaluation.getDelivery().getQuantityEvaluations() == 3){
            if (evaluation.getDelivery().getAverageScore() < 7)
                if (evaluation.getDelivery().getDeliveryType().equals(DeliveryType.REELABORACAO_PROPOSTA))
                    deliverablesService.updateDeliverableStatus(evaluation.getDelivery().getId(), DeliveryStatus.REELABORACAO_REPROVADA);
                else
                    deliverablesService.updateDeliverableStatus(evaluation.getDelivery().getId(), DeliveryStatus.REPROVADO);
            else
                deliverablesService.updateDeliverableStatus(evaluation.getDelivery().getId(), DeliveryStatus.APROVADO);
        }
        deliverablesRepository.save(evaluation.getDelivery());
        if (dto.comments() != null) evaluation.setComments(dto.comments());

        evaluation.setEvaluationDate(LocalDateTime.now());

        evaluationRepository.save(evaluation);
        alertService.createOrUpdateAlert(
                evaluation.getDelivery().getTcc().getStudent(),
                "Seu professor " + professor.getName() + " atualizou a avaliação. Verifique os detalhes da avaliação no sistema.",
                LocalDateTime.now(),
                AlertType.AVALIACAO_DISPONIVEL,
                null
        );
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
                        evaluation.getDelivery().getTcc().getId(),
                        evaluation.getDelivery().getDeliveryType(),
                        evaluation.getDelivery().getDeliveryStatus(),
                        evaluation.getDelivery().getBucketFileKey(),
                        evaluation.getDelivery().getDeliveryDate(),
                        evaluation.getDelivery().getQuantityEvaluations(),
                        evaluation.getDelivery().getAverageScore(),
                        evaluation.getDelivery().getUpdatedAt()
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
                evaluation.getEvaluationDate(),
                evaluation.getUpdatedAt()
        );
    }

    private void verifyProfessorPermission(Deliverables delivery, User professor) throws BadRequestException {
        if (!delivery.getTcc().getProfessor().getId().equals(professor.getId()) &&
        !delivery.getTcc().getDefensePanel().getProfessor2().getId().equals(professor.getId()) &&
        !delivery.getTcc().getDefensePanel().getProfessor3().getId().equals(professor.getId())) {
            throw new BadRequestException("You are not allowed to evaluate this delivery");
        }
    }
}
