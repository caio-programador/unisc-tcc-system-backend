package br.com.unisc.unisctccsystembackend.meetings.controller;

import br.com.unisc.unisctccsystembackend.meetings.entity.Meeting;
import br.com.unisc.unisctccsystembackend.meetings.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 🔹 GET /meetings?studentId=...&advisorId=...&start=...&end=...
    @GetMapping
    public List<Meeting> list(
            @RequestParam Optional<String> studentId,
            @RequestParam Optional<String> advisorId,
            @RequestParam Optional<LocalDateTime> start,
            @RequestParam Optional<LocalDateTime> end) {
        return service.list(studentId, advisorId, start, end);
    }

    // 🔹 GET /meetings/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getById(@PathVariable String id) {
        return service.getMeetingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 POST /meetings
    @PostMapping
    public Meeting create(@RequestBody Meeting meeting) {
        return service.saveMeeting(meeting);
    }

    // 🔹 PATCH /meetings/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Meeting> update(@PathVariable String id, @RequestBody Meeting patch) {
        return service.update(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 DELETE /meetings/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}
