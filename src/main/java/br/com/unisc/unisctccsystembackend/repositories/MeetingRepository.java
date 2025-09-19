package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByStudentIdAndMeetingDateAfter(Long studentId, LocalDateTime date);

    List<Meeting> findByProfessorIdAndMeetingDateAfter(Long professorId, LocalDateTime date);

    List<Meeting> findByStudentId(Long studentId);

    List<Meeting> findByProfessorId(Long professorId);

    Long countByStudentId(Long studentId);
}
