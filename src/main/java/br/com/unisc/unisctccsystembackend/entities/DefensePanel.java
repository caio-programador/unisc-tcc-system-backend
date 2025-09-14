package br.com.unisc.unisctccsystembackend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "defense_panel")
@Table(name = "defense_panel")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class DefensePanel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "professor_1_id")
    private User professor1;
    @ManyToOne
    @JoinColumn(name = "professor_2_id")
    private User professor2;
    @ManyToOne
    @JoinColumn(name = "professor_3_id")
    private User professor3;

    @OneToOne(mappedBy = "defensePanel")
    private TCCRelationships tccRelationships;
}
