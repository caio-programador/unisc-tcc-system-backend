package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsCreateDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.TCCRelationshipsUpdateDTO;
import br.com.unisc.unisctccsystembackend.service.TCCRelationshipsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relationships")
public class TCCRelationshipsController {
    @Autowired
    private TCCRelationshipsService tccRelationshipsService;

    @GetMapping()
    public ResponseEntity<Page<TCCRelationshipsResponseDTO>> getAllTCCRelationships(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(tccRelationshipsService.getAllTCCs(name, page, size));
    }

    @PostMapping()
    public ResponseEntity saveTCCRelationships(@Valid @RequestBody TCCRelationshipsCreateDTO tccRelationshipsCreateDTO) throws Exception {
        tccRelationshipsService.save(tccRelationshipsCreateDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TCCRelationshipsResponseDTO> getOneTCCById(@PathVariable Long id) {
        return ResponseEntity.ok(tccRelationshipsService.getOneTCCById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateTccRelationships(@PathVariable Long id, @RequestBody TCCRelationshipsUpdateDTO tccRelationshipsUpdateDTO) throws Exception {
        tccRelationshipsService.updateOneTCCById(tccRelationshipsUpdateDTO, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTccRelationships(@PathVariable Long id) {
        tccRelationshipsService.deleteOneTCCById(id);
        return ResponseEntity.noContent().build();
    }
}
