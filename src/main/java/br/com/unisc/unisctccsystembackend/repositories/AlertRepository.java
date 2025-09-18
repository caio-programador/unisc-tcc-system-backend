package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.Alert;
import br.com.unisc.unisctccsystembackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {
    List<Alert> findByDestinatarioAndDataGeracaoBetween(User user, LocalDateTime start, LocalDateTime end);
    List<Alert> findByDestinatario(User user);
}

