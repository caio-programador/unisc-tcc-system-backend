package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByStudent_IdAndMeetingDateAfter(Long student_id, OffsetDateTime meetingDate);

    List<Meeting> findAllByProfessor_IdAndMeetingDateAfter(Long professor_id, OffsetDateTime meetingDate);

    List<Meeting> findAllByStudent_Id(Long studentId);
    List<Meeting> findAllByProfessor_Id(Long professorId);

    Long countByStudentId(Long studentId);
}
