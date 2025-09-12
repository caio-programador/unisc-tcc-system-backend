package br.com.unisc.unisctccsystembackend.meetings.dto;

import java.time.LocalDateTime;

public class CreateMeetingRequest {
    public LocalDateTime meetingDate;
    public String subject;
    public String documentURL;
    public String studentId;
    public String advisorId;
}
