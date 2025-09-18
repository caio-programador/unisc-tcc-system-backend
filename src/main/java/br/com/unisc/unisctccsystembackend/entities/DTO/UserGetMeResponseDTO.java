package br.com.unisc.unisctccsystembackend.entities.DTO;

public record UserGetMeResponseDTO(
        Long id,
        String name,
        String email,
        String role,
        TCCRelationshipsResponseDTO tcc
) {}
