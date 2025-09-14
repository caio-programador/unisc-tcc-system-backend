package br.com.unisc.unisctccsystembackend.entities.DTO;


import br.com.unisc.unisctccsystembackend.entities.Admissibility;

import java.time.LocalDateTime;

public record TCCRelationshipsResponseDTO(Long id, String tccTitle,
                                          LocalDateTime proposalDeliveryDate, LocalDateTime tccDeliveryDate,
                                          LocalDateTime proposalAssessmentDate, LocalDateTime tccAssessmentDate,
                                          UserResponseDTO student,
                                          Admissibility admissibility,
                                          DefensePanelDTO defensePanel,
                                          UserResponseDTO professor) {
}
