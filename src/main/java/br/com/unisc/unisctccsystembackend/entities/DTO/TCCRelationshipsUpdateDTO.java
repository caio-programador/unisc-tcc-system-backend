package br.com.unisc.unisctccsystembackend.entities.DTO;

public record TCCRelationshipsUpdateDTO(String proposalDeliveryDate, String tccDeliveryDate,
                                        Long professorId,
                                        String tccTitle, Long professor1Id, Long professor2Id, Long professor3Id,
                                        String admissibility) {
}
