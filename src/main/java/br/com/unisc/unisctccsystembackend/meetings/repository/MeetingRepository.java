package br.com.unisc.unisctccsystembackend.meetings.repository;

import br.com.unisc.unisctccsystembackend.meetings.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, String> {
    List<Meeting> findByStudentId(String studentId);
    List<Meeting> findByAdvisorId(String advisorId);
    List<Meeting> findByMeetingDateBetween(LocalDateTime start, LocalDateTime end);
}
