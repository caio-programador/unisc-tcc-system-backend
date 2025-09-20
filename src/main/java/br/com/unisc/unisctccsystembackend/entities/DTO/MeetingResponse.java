package br.com.unisc.unisctccsystembackend.entities.DTO;

import br.com.unisc.unisctccsystembackend.entities.Meeting;
import java.time.OffsetDateTime;

public class MeetingResponse {
    public Long id;
    public OffsetDateTime meetingDate;
    public String subject;
    public String documentName;
    public String studentName;
    public String professorName;
    public String link;

    public static MeetingResponse from(Meeting m) {
        MeetingResponse r = new MeetingResponse();
        r.id = m.getId();
        r.meetingDate = m.getMeetingDate();
        r.subject = m.getSubject();
        r.documentName = m.getDocumentName();
        r.studentName = m.getStudent().getName();
        r.professorName = m.getProfessor().getName();
        r.link = m.getLink();
        return r;
    }
}
