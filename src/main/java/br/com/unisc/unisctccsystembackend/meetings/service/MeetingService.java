package br.com.unisc.unisctccsystembackend.meetings.service;

import br.com.unisc.unisctccsystembackend.meetings.entity.Meeting;
import br.com.unisc.unisctccsystembackend.meetings.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    /** Usado pelo Controller: lista com filtros opcionais */
    public List<Meeting> list(Optional<String> studentId,
                              Optional<String> advisorId,
                              Optional<LocalDateTime> start,
                              Optional<LocalDateTime> end) {

        // prioriza filtros específicos; senão aplica intervalo; senão lista tudo
        if (studentId.isPresent()) {
            return meetingRepository.findByStudentId(studentId.get());
        }
        if (advisorId.isPresent()) {
            return meetingRepository.findByAdvisorId(advisorId.get());
        }
        if (start.isPresent() && end.isPresent()) {
            return meetingRepository.findByMeetingDateBetween(start.get(), end.get());
        }
        return meetingRepository.findAll();
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public Optional<Meeting> getMeetingById(String id) {
        return meetingRepository.findById(id);
    }

    public Meeting saveMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    public void deleteMeeting(String id) {
        meetingRepository.deleteById(id);
    }

    public List<Meeting> getMeetingsByStudent(String studentId) {
        return meetingRepository.findByStudentId(studentId);
    }

    public List<Meeting> getMeetingsByAdvisor(String advisorId) {
        return meetingRepository.findByAdvisorId(advisorId);
    }

    /** PATCH simples: só atualiza campos não nulos */
    public Optional<Meeting> update(String id, Meeting patch) {
        return meetingRepository.findById(id).map(existing -> {
            if (patch.getMeetingDate() != null) existing.setMeetingDate(patch.getMeetingDate());
            if (patch.getSubject() != null)      existing.setSubject(patch.getSubject());
            if (patch.getDocumentURL() != null)  existing.setDocumentURL(patch.getDocumentURL());
            if (patch.getStudentId() != null)    existing.setStudentId(patch.getStudentId());
            if (patch.getAdvisorId() != null)    existing.setAdvisorId(patch.getAdvisorId());
            return meetingRepository.save(existing);
        });
    }
}
