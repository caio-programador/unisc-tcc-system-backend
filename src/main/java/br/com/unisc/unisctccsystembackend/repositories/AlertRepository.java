package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.Alert;
import br.com.unisc.unisctccsystembackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserAndGeneratedAtBetween(User user, LocalDateTime start, LocalDateTime end);
    List<Alert> findByUser(User user);
}

