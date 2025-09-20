package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.AlertType;
import br.com.unisc.unisctccsystembackend.entities.DTO.MeetingBodyDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.MeetingResponse;
import br.com.unisc.unisctccsystembackend.entities.Meeting;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.entities.UserRole;
import br.com.unisc.unisctccsystembackend.repositories.MeetingRepository;
import br.com.unisc.unisctccsystembackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final AlertService alertService;

    public MeetingService(MeetingRepository meetingRepository,
                          S3Service s3Service,
                          UserRepository userRepository, AlertService alertService) {
        this.meetingRepository = meetingRepository;
        this.s3Service = s3Service;
        this.userRepository = userRepository;
        this.alertService = alertService;
    }

    public List<Meeting> list(String start,
                              User currentUser) {
        if (currentUser.getRole().equals(UserRole.PROFESSOR)) {
            if (!start.isEmpty()) {
                Instant instant = Instant.parse(start);
                OffsetDateTime dateTime = instant.atZone(ZoneOffset.UTC).toOffsetDateTime();
                return meetingRepository.findAllByProfessor_IdAndMeetingDateAfter(currentUser.getId(),
                        dateTime);
            } else {
                return meetingRepository.findAllByProfessor_Id(currentUser.getId());
            }
        } else {
            if (!start.isEmpty()) {
                Instant instant = Instant.parse(start);
                OffsetDateTime dateTime = instant.atZone(ZoneId.of("America/Sao_Paulo")).toOffsetDateTime();
                return meetingRepository.findAllByStudent_IdAndMeetingDateAfter(currentUser.getId(),
                        dateTime);
            } else {
                return meetingRepository.findAllByStudent_Id(currentUser.getId());
            }
        }

    }

    public MeetingResponse getMeetingById(Long id) {
        return meetingRepository.findById(id).map(MeetingResponse::from).orElseThrow(() ->
                new EntityNotFoundException("Meeting not found with id " + id));
    }

    public void saveMeeting(MeetingBodyDTO meetingBody) throws BadRequestException {
        User professor = userRepository.findById(meetingBody.professorId()).orElseThrow(() ->
                new EntityNotFoundException("User not found with id " + meetingBody.professorId()));
        User student = userRepository.findById(meetingBody.studentId()).orElseThrow(() ->
                new EntityNotFoundException("User not found with id " + meetingBody.studentId()));
        if (!professor.getRole().equals(UserRole.PROFESSOR)) {
            throw new BadRequestException("User with id " + meetingBody.professorId() + " is not a professor");
        }
        if (!student.getRole().equals(UserRole.ALUNO)) {
            throw new BadRequestException("User with id " + meetingBody.studentId() + " is not a student");
        }

        Instant instant = Instant.parse(meetingBody.meetingDate());
        OffsetDateTime dateTime = instant.atZone(ZoneId.of("America/Sao_Paulo")).toOffsetDateTime();
        Meeting meeting = new Meeting();
        meeting.setMeetingDate(dateTime);
        meeting.setSubject(meetingBody.subject());
        meeting.setProfessor(professor);
        meeting.setStudent(student);
        meeting.setLink(meetingBody.link());

        alertService.createOrUpdateAlert(professor,
                "Nova reunião agendada com " + student.getName() + " para falar sobre " + meeting.getSubject(),
                dateTime.toLocalDateTime(),
                AlertType.NOVA_REUNIAO, null);
        alertService.createOrUpdateAlert(student,
                "Nova reunião agendada com " + professor.getName() + " para falar sobre " + meeting.getSubject(),
                dateTime.toLocalDateTime(),
                AlertType.NOVA_REUNIAO, null);

        meetingRepository.save(meeting);
    }

    public void deleteMeeting(Long id, User currentUser) throws BadRequestException {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Meeting not found with id " + id));
        if(!currentUser.getId().equals(meeting.getProfessor().getId())) {
            throw new BadRequestException("User with id " + currentUser.getId() + " is not a professor");
        }
        meetingRepository.delete(meeting);
        alertService.createOrUpdateAlert(meeting.getStudent(), "A reunião sobre " + meeting.getSubject() + " foi cancelada",
                LocalDateTime.now(),
                AlertType.REUNIAO_CANCELADA, null);
        alertService.createOrUpdateAlert(meeting.getProfessor(), "A reunião sobre " + meeting.getSubject() + " foi cancelada",
                LocalDateTime.now(),
                AlertType.REUNIAO_CANCELADA, null);
    }

    public void updateMeeting(Long id, MultipartFile file, User currentUser) throws BadRequestException {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Meeting not found with id " + id));

        if(!currentUser.getId().equals(meeting.getProfessor().getId())) {
            throw new BadRequestException("User with id " + currentUser.getId() + " is not a professor");
        }

        String fileUrl;

        if(meeting.getDocumentName() != null){
            if(!meeting.getDocumentName().isEmpty()) {
                try {
                    s3Service.deleteFile(meeting.getDocumentName());
                } catch (Exception e) {
                    throw new BadRequestException("Failed to delete existing file: " + e.getMessage());
                }
            }
        }

        try {
            fileUrl = s3Service.uploadFile(file);
        }catch (Exception e) {
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }

        meeting.setDocumentName(fileUrl);
        meetingRepository.save(meeting);

        alertService.createOrUpdateAlert(meeting.getStudent(),
                "Há uma nova ata de reunião disponível para a reunião sobre " + meeting.getSubject(),
                LocalDateTime.now(),
                AlertType.NOVA_REUNIAO, null);
    }

    public Long countMeetings(User user) {
        return meetingRepository.countByStudentId(user.getId());
    }

    public List<Meeting> getLimitedMeetings(User user) {
        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"));
        return this.list(now.toString(), user).stream().limit(3).toList();
    }
}
