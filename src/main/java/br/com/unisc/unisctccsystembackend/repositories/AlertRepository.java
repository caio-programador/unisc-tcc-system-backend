package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.Alert;
import br.com.unisc.unisctccsystembackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserAndAlertDateBetweenOrderByAlertDateDesc(User user, LocalDateTime start, LocalDateTime end);
    List<Alert> findByUserOrderByAlertDate(User user);
    List<Alert> findTop3ByUserAndAlertDateBetweenAndIsReadOrderByAlertDate(User user, LocalDateTime start, LocalDateTime end, boolean isRead);

    List<Alert> findByUserAndIsReadFalse(User user);
}

