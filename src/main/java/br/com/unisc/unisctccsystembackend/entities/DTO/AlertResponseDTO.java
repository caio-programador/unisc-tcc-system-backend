package br.com.unisc.unisctccsystembackend.entities.DTO;

import br.com.unisc.unisctccsystembackend.entities.AlertType;
import java.time.LocalDateTime;

public record AlertResponseDTO(
        String id,
        String mensagem,
        LocalDateTime dataGeracao,
        Boolean isLido,
        AlertType tipoAlerta,
        Long destinatarioId
) {}
