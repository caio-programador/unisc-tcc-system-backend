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
            alerts = alertRepository.findByUserAndAlertDateBetweenOrderByAlertDateDesc(user, start, end);
        } else {
            alerts = alertRepository.findByUserOrderByAlertDate(user);
        }
        return alerts.stream().map(this::toDTO).toList();
    }
    private AlertResponseDTO toDTO(Alert alert) {
        return new AlertResponseDTO(
                alert.getId(),
                alert.getMessage(),
                alert.getGeneratedAt(),
                alert.getIsRead(),
                alert.getType(),
                alert.getUser().getId(),
                alert.getAlertDate()
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

    public void createOrUpdateAlert(User user, String message, LocalDateTime alertDate, AlertType type, Long alertId) {
        Alert alert = new Alert();

        if(alertId != null) {
            alert = alertRepository.findById(alertId).orElse(new Alert());
        }

        alert.setUser(user);
        alert.setMessage(message);
        alert.setAlertDate(alertDate);
        alert.setType(type);
        alert.setGeneratedAt(LocalDateTime.now());
        alert.setIsRead(false);

        alertRepository.save(alert);
    }

    public List<AlertResponseDTO> getLimitedAlerts(User user) {
        List<Alert> alerts = alertRepository.findTop3ByUserAndAlertDateBetweenAndIsReadOrderByAlertDate(user, LocalDateTime.now(), LocalDateTime.now().plusDays(7), false);
        return alerts.stream().map(this::toDTO).toList();
    }

    public void markAllAlertsAsRead(User user) {
        List<Alert> alerts = alertRepository.findByUserAndIsReadFalse(user);
        for (Alert alert : alerts) {
            alert.setIsRead(true);
        }
        alertRepository.saveAll(alerts);
    }
}
