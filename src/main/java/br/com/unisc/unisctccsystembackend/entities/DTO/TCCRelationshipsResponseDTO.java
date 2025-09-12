package br.com.unisc.unisctccsystembackend.entities.DTO;


import java.time.LocalDateTime;

public record TCCRelationshipsResponseDTO(Long id, String tccTitle,
                                          LocalDateTime proposalDeliveryDate, LocalDateTime tccDeliveryDate,
                                          LocalDateTime proposalAssessmentDate, LocalDateTime tccAssessmentDate,
                                          UserResponseDTO student,

                                          UserResponseDTO professor) {
}
