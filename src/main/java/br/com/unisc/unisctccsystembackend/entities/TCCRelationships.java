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

    @OneToOne()
    @JoinColumn(name = "student_id", unique = true)
    private User student;

    @ManyToOne()
    @JoinColumn(name = "professor_id")
    private User professor;

    @OneToMany(mappedBy = "tcc", cascade =  CascadeType.ALL)
    private List<Deliverables> deliverables;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "defense_panel_id")
    private DefensePanel defensePanel;

    @Column(nullable = true)
    private Admissibility admissibility;

}
