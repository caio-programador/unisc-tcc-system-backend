package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, String> {
    List<Meeting> findByStudentId(String studentId);
    List<Meeting> findByAdvisorId(String advisorId);
    List<Meeting> findByMeetingDateBetween(LocalDateTime start, LocalDateTime end);
}
