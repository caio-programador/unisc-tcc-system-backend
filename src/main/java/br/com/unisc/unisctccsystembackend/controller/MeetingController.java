package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.Meeting;
import br.com.unisc.unisctccsystembackend.service.MeetingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    // GET /meetings?studentId=&advisorId=&start=&end=
    @GetMapping
    public ResponseEntity<List<Meeting>> list(
            @RequestParam Optional<String> studentId,
            @RequestParam Optional<String> advisorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> end) {

        List<Meeting> result = service.list(studentId, advisorId, start, end);
        return result.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(result);
    }

    // GET /meetings/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getById(@PathVariable String id) {
        return service.getMeetingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /meetings
    @PostMapping
    public ResponseEntity<Meeting> create(@RequestBody Meeting meeting) {
        Meeting saved = service.saveMeeting(meeting);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    // PATCH /meetings/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Meeting> update(@PathVariable String id, @RequestBody Meeting patch) {
        return service.update(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /meetings/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.getMeetingById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}
