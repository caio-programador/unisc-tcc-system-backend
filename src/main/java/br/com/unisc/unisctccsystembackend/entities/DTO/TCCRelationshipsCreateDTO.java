package br.com.unisc.unisctccsystembackend.entities.DTO;

public record TCCRelationshipsCreateDTO(String proposalDeliveryDate, String tccDeliveryDate,
                                        Long studentId,
                                        Long professorId) {
}