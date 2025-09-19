package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.DTO.MeetingBodyDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.MeetingResponse;
import br.com.unisc.unisctccsystembackend.entities.Meeting;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.service.MeetingService;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService service;

    public MeetingController(MeetingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> list(
           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> start,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        List<Meeting> result = service.list(start, user);
        List<MeetingResponse> responseList = result.stream().map(MeetingResponse::from).toList();
        return result.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMeetingById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody MeetingBodyDTO body
            ) throws BadRequestException {
        service.saveMeeting(body);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{id}/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateDocument(
            @PathVariable Long id,
            @RequestParam(name = "file") MultipartFile file,
            Authentication authentication
            ) throws BadRequestException {
        User user = (User) authentication.getPrincipal();
        service.updateMeeting(id, file, user);
        return ResponseEntity.noContent().build();
    };

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) throws BadRequestException{
        User user = (User) authentication.getPrincipal();
        service.deleteMeeting(id, user);
        return ResponseEntity.noContent().build();
    }
}
