package br.com.unisc.unisctccsystembackend.entities.DTO;

import java.time.LocalDateTime;
public record EvaluationResponseDTO(
        Long id,
        DeliveryResponseDTO delivery,
        UserResponseDTO professor,
        Double introduction,
        Double goals,
        Double bibliographyRevision,
        Double methodology,
        Double total,
        String comments,
        LocalDateTime evaluationDate,
        LocalDateTime updatedAt
) {}