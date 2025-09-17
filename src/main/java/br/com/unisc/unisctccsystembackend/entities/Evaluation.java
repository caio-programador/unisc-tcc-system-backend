package br.com.unisc.unisctccsystembackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "evaluations")
@Table(name = "evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false)
    private Deliverables delivery;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor; // avaliador (orientador ou banca futuramente)

    private Double introduction;
    private Double goals;
    private Double bibliographyRevision;
    private Double methodology;
    private Double total;

    private String comments;

    private LocalDateTime evaluationDate;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

