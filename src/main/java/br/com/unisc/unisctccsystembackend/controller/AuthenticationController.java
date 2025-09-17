package br.com.unisc.unisctccsystembackend.controller;


import br.com.unisc.unisctccsystembackend.entities.DTO.*;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.repositories.UserRepository;
import br.com.unisc.unisctccsystembackend.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.email(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserGetMeResponseDTO> me(org.springframework.security.core.Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new UserGetMeResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getTccRelationships() != null ? new TCCRelationshipsResponseDTO(
                        user.getTccRelationships().getId(),
                        user.getTccRelationships().getTccTitle(),
                        user.getTccRelationships().getProposalDeliveryDate(),
                        user.getTccRelationships().getTccDeliveryDate(),
                        user.getTccRelationships().getProposalAssessmentDate(),
                        user.getTccRelationships().getTccAssessmentDate(),
                        null,
                        user.getTccRelationships().getAdmissibility(),
                        new DefensePanelDTO(
                                user.getTccRelationships().getDefensePanel().getProfessor1().getId(),
                                user.getTccRelationships().getDefensePanel().getProfessor1().getName(),
                                user.getTccRelationships().getDefensePanel().getProfessor2().getId(),
                                user.getTccRelationships().getDefensePanel().getProfessor2().getName(),
                                user.getTccRelationships().getDefensePanel().getProfessor3().getId(),
                                user.getTccRelationships().getDefensePanel().getProfessor3().getName()
                        ),
                        null
                ): null
        ));
    }
}
