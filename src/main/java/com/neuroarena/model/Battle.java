package com.neuroarena.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "battles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Battle {

    @Id
    @Column(length = 36)
    private String battleId;  // Changed from Long to String (UUID)

    @Column(unique = true)
    private String roomCode;

    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;  // Changed from RoomType object to Integer ID

    private String hostUsername;

    private String status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private Integer currentQuestionNumber;

    private LocalDateTime createdAt;

    private Double pointsMultiplier;
}
