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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long battlePlayerId;

    @ManyToOne
    @JoinColumn(name = "battle_id")
    private Battle battle;

    private Long playerId;
    private String username;

    private LocalDateTime joinedAt;
}
