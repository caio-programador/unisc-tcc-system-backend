package br.com.unisc.unisctccsystembackend.entities;

public enum UserRole {
    ALUNO("aluno"),
    PROFESSOR("professor"),
    COORDENADOR("coordenador");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}