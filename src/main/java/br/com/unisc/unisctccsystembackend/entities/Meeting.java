package br.com.unisc.unisctccsystembackend.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private LocalDateTime meetingDate;

    @Column(nullable = false, length = 2000)
    private String subject;

    private String documentName;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
}
