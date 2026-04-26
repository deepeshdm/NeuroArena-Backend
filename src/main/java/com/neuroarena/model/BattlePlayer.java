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
    private String battlePlayerId;

    @Column(name = "battle_id", nullable = false, length = 36)
    private String battleId;

    @Column(name = "player_id", nullable = false, length = 36)
    private String playerId;

    private String username;

    @Column(name = "avatar_icon_url", length = 500)
    private String avatarIconUrl;  // ← ADD THIS

    private LocalDateTime joinedAt;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "WAITING";  // WAITING, READY, IN_GAME
}