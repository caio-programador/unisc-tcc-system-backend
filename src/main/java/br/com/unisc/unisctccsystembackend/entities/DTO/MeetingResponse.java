package br.com.unisc.unisctccsystembackend.entities.DTO;

import br.com.unisc.unisctccsystembackend.entities.Meeting;
import java.time.LocalDateTime;

public class MeetingResponse {
    public String id;
    public LocalDateTime meetingDate;
    public String subject;
    public String documentURL;
    public String studentId;
    public String advisorId;

    public static MeetingResponse from(Meeting m) {
        MeetingResponse r = new MeetingResponse();
        r.id = m.getId();
        r.meetingDate = m.getMeetingDate();
        r.subject = m.getSubject();
        r.documentURL = m.getDocumentURL();
        r.studentId = m.getStudentId();
        r.advisorId = m.getAdvisorId();
        return r;
    }
}
