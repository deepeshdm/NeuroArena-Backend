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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long battleId;

    @Column(unique = true)
    private String roomCode;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    private String hostUsername;

    private String status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private Integer currentQuestionNumber;

    private LocalDateTime createdAt;

    private Double pointsMultiplier;
}
