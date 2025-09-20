package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.*;
import br.com.unisc.unisctccsystembackend.entities.DTO.AlertResponseDTO;
import br.com.unisc.unisctccsystembackend.repositories.AlertRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    public List<AlertResponseDTO> getAlerts(User user, LocalDateTime start, LocalDateTime end) {
        List<Alert> alerts;

        if (start != null && end != null) {
            alerts = alertRepository.findByUserAndGeneratedAtBetween(user, start, end);
        } else {
            alerts = alertRepository.findByUser(user);
        }

        return alerts.stream().map(this::toDTO).toList();
    }

    private AlertResponseDTO toDTO(Alert alerta) {
        return new AlertResponseDTO(
                alerta.getId(),
                alerta.getMessage(),
                alerta.getGeneratedAt(),
                alerta.getIsRead(),
                alerta.getType(),
                alerta.getUser().getId(),
                alerta.getAlertDate()
        );
    }

    public void markAlertAsRead(User user, Long id) throws BadRequestException {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found"));

        if(!alert.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot mark this alert as read");
        }

        alert.setIsRead(true);
        alertRepository.save(alert);
    }

    public void deleteAlert(User user, Long id) throws BadRequestException {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found"));

        if(!alert.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot delete this alert");
        }

        alertRepository.delete(alert);
    }
}
