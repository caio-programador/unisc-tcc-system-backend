package br.com.unisc.unisctccsystembackend.entities.DTO;

public record TCCRelationshipsUpdateDTO(String proposalDeliveryDate, String tccDeliveryDate,
                                        Long professorId,
                                        String tccTitle) {
}
