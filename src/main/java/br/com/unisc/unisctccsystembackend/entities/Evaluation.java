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
}

