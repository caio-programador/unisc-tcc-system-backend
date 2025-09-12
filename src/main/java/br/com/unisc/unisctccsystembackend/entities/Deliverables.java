package br.com.unisc.unisctccsystembackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

}
