package br.com.unisc.unisctccsystembackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "tcc_relationships")
@Table(name = "tcc_relationships")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TCCRelationships {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tccTitle;
    private LocalDateTime proposalDeliveryDate;
    private LocalDateTime tccDeliveryDate;
    private LocalDateTime proposalAssessmentDate;
    private LocalDateTime tccAssessmentDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", unique = true)
    private User student;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "professor_id")
    private User professor;

    @OneToMany(mappedBy = "tcc")
    private List<Deliverables> deliverables;

}
