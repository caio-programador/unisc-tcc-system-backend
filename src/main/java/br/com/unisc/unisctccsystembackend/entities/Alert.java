package br.com.unisc.unisctccsystembackend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Alert {

    @Id
    private String id;

    @Column(name = "message", nullable = false, length = 500)
    private String mensagem;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime dataGeracao;

    @Column(name = "is_read")
    private Boolean isLido = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private AlertType tipoAlerta;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User destinatario;
}
