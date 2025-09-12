package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.DTO.DeliveryCreateDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.DeliveryResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.DeliveryType;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.service.DeliverablesService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/deliverables")
public class DeliverableController {
    @Autowired
    private DeliverablesService deliverablesService;

    @GetMapping()
    public ResponseEntity<List<DeliveryResponseDTO>> getMineDeliverables(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<DeliveryResponseDTO> deliverables = deliverablesService.getAllDeliverablesByStudentId(user.getId());
        return ResponseEntity.ok(deliverables);
    }

    @GetMapping("/{tccId}")
    public ResponseEntity<List<DeliveryResponseDTO>> getDeliverablesByStudentId(@PathVariable Long tccId) {
        List<DeliveryResponseDTO> deliverables = deliverablesService.getAllDeliverablesByTccId(tccId);
        return ResponseEntity.ok(deliverables);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createDeliverable(@RequestParam(name = "tccTitle") String tccTitle,
                                                  @RequestParam(name = "file") MultipartFile file,
                                                  @RequestParam(name = "deliveryType") DeliveryType deliveryType,
                                                  @RequestParam(name = "tccId") Long tccId) throws BadRequestException {
        DeliveryCreateDTO deliverableDTO = new DeliveryCreateDTO(
                file,
                tccId,
                tccTitle,
                deliveryType
        );
        deliverablesService.saveDeliverable(deliverableDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{deliverableId}")
    public ResponseEntity<Void> deleteDeliverable(@PathVariable Long deliverableId) throws BadRequestException {
        deliverablesService.deleteDeliverable(deliverableId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/{deliverableId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateDeliverable(@PathVariable Long deliverableId,
                                                  @RequestParam(name = "tccTitle", required = false) String tccTitle,
                                                  @RequestParam(name = "file", required = false) MultipartFile file,
                                                  @RequestParam(name = "deliveryType", required = false) DeliveryType deliveryType,
                                                  @RequestParam(name = "tccId", required = false) Long tccId
                                                  ) throws BadRequestException {
        DeliveryCreateDTO deliverableDTO = new DeliveryCreateDTO(
                file,
                tccId,
                tccTitle,
                deliveryType
        );
        deliverablesService.updateDeliverableById(deliverableId, deliverableDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String key) throws Exception {
        byte[] fileData = deliverablesService.downloadFile(key);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + key + "\"")
                .body(fileData);
    }
}
