package com.neuroarena.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "battle_players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattlePlayer {

    @Id
    @Column(length = 36)
    private String battlePlayerId;  // Changed from Long to String (UUID)

    @Column(name = "battle_id", nullable = false, length = 36)
    private String battleId;   // NOT Long

    @Column(name = "player_id", nullable = false, length = 36)
    private String playerId;   // NOT Long

    private String username;

    private LocalDateTime joinedAt;
}
