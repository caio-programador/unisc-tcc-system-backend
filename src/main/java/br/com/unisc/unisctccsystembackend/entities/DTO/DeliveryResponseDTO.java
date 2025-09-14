package br.com.unisc.unisctccsystembackend.entities.DTO;

import br.com.unisc.unisctccsystembackend.entities.DeliveryStatus;
import br.com.unisc.unisctccsystembackend.entities.DeliveryType;

import java.time.LocalDateTime;

public record DeliveryResponseDTO(
        Long id,
        Long tccId,
        DeliveryType deliveryType,
        DeliveryStatus deliveryStatus,
        String bucketFileKey,
        LocalDateTime deliveryDate,
        int quantityEvaluations,
        Double averageScore
) {
}
