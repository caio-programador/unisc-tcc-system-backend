package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.entities.DTO.AlertResponseDTO;
import br.com.unisc.unisctccsystembackend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    @Autowired
    private AlertService alertaService;

    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> getAlerts(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(alertaService.getAlerts(user, start, end));
    }
}