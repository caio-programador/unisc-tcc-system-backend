package br.com.unisc.unisctccsystembackend.entities.DTO;

import br.com.unisc.unisctccsystembackend.entities.Meeting;
import java.time.LocalDateTime;

public class MeetingResponse {
    public Long id;
    public LocalDateTime meetingDate;
    public String subject;
    public String documentName;
    public String studentName;
    public String professorName;

    public static MeetingResponse from(Meeting m) {
        MeetingResponse r = new MeetingResponse();
        r.id = m.getId();
        r.meetingDate = m.getMeetingDate();
        r.subject = m.getSubject();
        r.documentName = m.getDocumentName();
        r.studentName = m.getStudent().getName();
        r.professorName = m.getProfessor().getName();
        return r;
    }
}
