package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.DTO.CountMeetingsResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.MeetingBodyDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.MeetingResponse;
import br.com.unisc.unisctccsystembackend.entities.Meeting;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.service.MeetingService;
import br.com.unisc.unisctccsystembackend.service.S3Service;
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
    private final S3Service s3Service;

    public MeetingController(MeetingService service, S3Service s3Service) {
        this.service = service;
        this.s3Service = s3Service;
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> list(
           @RequestParam(name = "startDate", required = false, defaultValue = "") String startDate,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        List<Meeting> result = service.list(startDate, user);
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

    @GetMapping("/download/{documentName}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String documentName) throws Exception {
        byte[] document = s3Service.downloadFile(documentName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(document);
    }

    @GetMapping("/count")
    public ResponseEntity<CountMeetingsResponseDTO> getCountMeetings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long count = service.countMeetings(user);
        CountMeetingsResponseDTO responseDTO = new CountMeetingsResponseDTO(count);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/limited")
    public ResponseEntity<List<MeetingResponse>> getLimitedMeetings(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Meeting> meetings = service.getLimitedMeetings(user);
        List<MeetingResponse> responseList = meetings.stream().map(MeetingResponse::from).toList();
        return ResponseEntity.ok(responseList);
    }
}
