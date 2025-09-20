package br.com.unisc.unisctccsystembackend.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "meetings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime meetingDate;

    @Column(nullable = false, length = 2000)
    private String subject;

    private String documentName;

    private String link;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
}
