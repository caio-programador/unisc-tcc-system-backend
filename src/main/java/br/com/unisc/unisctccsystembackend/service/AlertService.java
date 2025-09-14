package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.*;
import br.com.unisc.unisctccsystembackend.entities.DTO.AlertResponseDTO;
import br.com.unisc.unisctccsystembackend.repositories.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertaRepository;

    public List<AlertResponseDTO> getAlerts(User user, LocalDateTime start, LocalDateTime end) {
        List<Alert> alerts;

        if (start != null && end != null) {
            // Filtra por intervalo de tempo
            alerts = alertaRepository.findByDestinatarioAndDataGeracaoBetween(user, start, end);
        } else {
            // Busca todos os alerts do usuário
            alerts = alertaRepository.findByDestinatario(user);
        }

        return alerts.stream().map(this::toDTO).toList();
    }

    private AlertResponseDTO toDTO(Alert alerta) {
        return new AlertResponseDTO(
                alerta.getId(),
                alerta.getMensagem(),
                alerta.getDataGeracao(),
                alerta.getIsLido(),
                alerta.getTipoAlerta(),
                alerta.getDestinatario().getId()
        );
    }
}
