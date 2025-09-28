package br.com.unisc.unisctccsystembackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "deliverables")
@Table(name = "deliverables")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Deliverables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "tcc_id")
    private TCCRelationships tcc;

    private DeliveryType deliveryType;

    private LocalDateTime deliveryDate;

    private DeliveryStatus deliveryStatus;

    private String bucketFileKey;

    private int quantityEvaluations;
    private Double totalScore;
    private Double averageScore;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "delivery", cascade =  CascadeType.ALL)
    private List<Evaluation> evaluations;

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
