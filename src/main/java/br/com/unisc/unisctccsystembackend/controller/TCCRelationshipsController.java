package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.Admissibility;
import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsCreateDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsUpdateDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.UpdateAdmissibilityDTO;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.service.TCCRelationshipsService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/relationships")
public class TCCRelationshipsController {
    @Autowired
    private TCCRelationshipsService tccRelationshipsService;

    @GetMapping()
    public ResponseEntity<Page<TCCRelationshipsResponseDTO>> getAllTCCRelationships(@RequestParam(defaultValue = "") String name,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(tccRelationshipsService.getAllTCCs(name, page, size, user));
    }

    @PostMapping()
    public ResponseEntity<Void> saveTCCRelationships(@Valid @RequestBody TCCRelationshipsCreateDTO tccRelationshipsCreateDTO) throws Exception {
        tccRelationshipsService.save(tccRelationshipsCreateDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TCCRelationshipsResponseDTO> getOneTCCById(@PathVariable Long id) {
        return ResponseEntity.ok(tccRelationshipsService.getOneTCCById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<TCCRelationshipsResponseDTO> getOneTCCByStudentId(@PathVariable Long studentId) throws BadRequestException {
        return ResponseEntity.ok(tccRelationshipsService.getOneTCCByStudentId(studentId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateTccRelationships(@PathVariable Long id, @RequestBody TCCRelationshipsUpdateDTO tccRelationshipsUpdateDTO) throws Exception {
        tccRelationshipsService.updateOneTCCById(tccRelationshipsUpdateDTO, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTccRelationships(@PathVariable Long id) {
        tccRelationshipsService.deleteOneTCCById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admissibility/{id}")
    public ResponseEntity<Void> updateAdmissibility(@PathVariable Long id, @RequestBody @Valid UpdateAdmissibilityDTO updateAdmissibilityDTO) {
        tccRelationshipsService.updateAdmissibility(id, updateAdmissibilityDTO.admissibility());
        return ResponseEntity.noContent().build();
    }
}
