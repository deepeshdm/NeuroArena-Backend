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

    @Column(name = "battle_id", nullable = false, length = 36)
    private String battleId;   // NOT Long

    @Column(name = "player_id", nullable = false, length = 36)
    private String playerId;   // NOT Long
    private String username;

    private Integer finalRank;
    private Integer totalScore;
    private Integer correctAnswers;

    private Double accuracy;
    private Double avgResponseTimeMs;

    private LocalDateTime completedAt;
}
