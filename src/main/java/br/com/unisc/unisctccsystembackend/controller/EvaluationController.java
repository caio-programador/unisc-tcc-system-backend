package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.entities.DTO.*;
import br.com.unisc.unisctccsystembackend.service.EvaluationService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @GetMapping("/{deliveryId}")
    public ResponseEntity<List<EvaluationResponseDTO>> getEvaluations(@PathVariable Long deliveryId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByDeliveryId(deliveryId));
    }

    @GetMapping("/{deliveryId}/{professorId}")
    public ResponseEntity<EvaluationResponseDTO> getEvaluationByProfessor(@PathVariable Long deliveryId,
                                                                          @PathVariable Long professorId) {
        return ResponseEntity.ok(evaluationService.getEvaluationByDeliveryIdAndProfessorId(deliveryId, professorId));
    }

    @PostMapping
    public ResponseEntity<EvaluationResponseDTO> createEvaluation(@RequestBody EvaluationRequestDTO dto, Authentication authentication) throws BadRequestException {
        User professor = (User) authentication.getPrincipal();
        return ResponseEntity.ok(evaluationService.createEvaluation(dto, professor));
    }

    @PatchMapping("/{evaluationId}")
    public ResponseEntity<EvaluationResponseDTO> updateEvaluation(@PathVariable Long evaluationId,
                                                                  @RequestBody EvaluationRequestDTO dto,
                                                                  Authentication authentication) throws BadRequestException {
        User professor = (User) authentication.getPrincipal();
        return ResponseEntity.ok(evaluationService.updateEvaluation(evaluationId, dto, professor));
    }
}
