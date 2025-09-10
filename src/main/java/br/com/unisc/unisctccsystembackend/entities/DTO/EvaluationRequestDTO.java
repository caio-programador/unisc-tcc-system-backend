package br.com.unisc.unisctccsystembackend.entities.DTO;


public record EvaluationRequestDTO(
        Long deliveryId,
        Double introduction,
        Double goals,
        Double bibliographyRevision,
        Double methodology,
        Double total,
        String comments
) {}