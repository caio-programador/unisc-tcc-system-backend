package br.com.unisc.unisctccsystembackend.entities.DTO;

public record MeetingBodyDTO (
        String meetingDate,
        String subject,
        Long professorId,
        Long studentId
) {
}
