package br.com.unisc.unisctccsystembackend.entities.DTO;

import br.com.unisc.unisctccsystembackend.entities.AlertType;
import java.time.LocalDateTime;

public record AlertResponseDTO(
        Long id,
        String message,
        LocalDateTime generatedAt,
        Boolean isRead,
        AlertType type,
        Long userId,
        LocalDateTime alertDate
) {}
