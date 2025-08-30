package br.com.unisc.unisctccsystembackend.entities.DTO;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String role
) {}
