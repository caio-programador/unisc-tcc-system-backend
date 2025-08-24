package br.com.unisc.unisctccsystembackend.entities.DTO;

import br.com.unisc.unisctccsystembackend.entities.UserRole;

public record RegisterDTO(String name, String email, String password, UserRole role) {
}
