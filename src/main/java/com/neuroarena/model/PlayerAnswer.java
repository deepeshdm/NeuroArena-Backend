package com.neuroarena.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "player_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerAnswer {

    @Id
    @Column(length = 36)
    private String playerAnswerId;  // Changed from Long to String (UUID)

    @Column(name = "battle_id", nullable = false, length = 36)
    private String battleId;   // NOT Long

    @Column(name = "player_id", nullable = false, length = 36)
    private String playerId;   // NOT Long

    @Column(name = "question_id", nullable = false, length = 36)
    private String questionId;   // NOT Long

    @Column(name = "selected_answer_id", nullable = false, length = 36)
    private String selectedAnswerId;   // NOT Long

    private Integer questionNumber;

    private Integer responseTimeMs;

    private Integer pointsEarned;

    private Boolean isCorrect;

    private LocalDateTime submittedAt;
}
