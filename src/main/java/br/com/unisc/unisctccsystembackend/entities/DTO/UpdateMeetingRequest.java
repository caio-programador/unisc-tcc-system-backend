package br.com.unisc.unisctccsystembackend.entities.DTO;

import java.time.LocalDateTime;

public class UpdateMeetingRequest {
    public LocalDateTime meetingDate;
    public String subject;
    public String documentURL;
    public String studentId;
    public String advisorId;
}
