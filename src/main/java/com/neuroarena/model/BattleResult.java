package com.neuroarena.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "battle_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleResult {

    @Id
    @Column(length = 36)
    private String battleResultId;  // Changed from Long to String (UUID)

    @ManyToOne
    @JoinColumn(name = "battle_id")
    private Battle battle;

    private Long playerId;
    private String username;

    private Integer finalRank;
    private Integer totalScore;
    private Integer correctAnswers;

    private Double accuracy;
    private Double avgResponseTimeMs;

    private LocalDateTime completedAt;
}
