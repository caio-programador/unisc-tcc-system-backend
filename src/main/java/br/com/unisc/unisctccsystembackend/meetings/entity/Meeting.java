package br.com.unisc.unisctccsystembackend.meetings.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private LocalDateTime meetingDate;

    @Column(nullable = false, length = 2000)
    private String subject;

    @Column(length = 1000)
    private String documentURL;

    @Column(nullable = false, length = 36)
    private String studentId;

    @Column(nullable = false, length = 36)
    private String advisorId;

    protected Meeting() {} // <— JPA precisa do no-args

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDateTime getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDateTime meetingDate) { this.meetingDate = meetingDate; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDocumentURL() { return documentURL; }
    public void setDocumentURL(String documentURL) { this.documentURL = documentURL; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getAdvisorId() { return advisorId; }
    public void setAdvisorId(String advisorId) { this.advisorId = advisorId; }
}
