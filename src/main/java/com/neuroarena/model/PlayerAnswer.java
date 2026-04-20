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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerAnswerId;

    @ManyToOne
    @JoinColumn(name = "battle_id")
    private Battle battle;

    private Long playerId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "selected_answer_id")
    private Answer selectedAnswer;

    private Integer questionNumber;

    private Integer responseTimeMs;

    private Integer pointsEarned;

    private Boolean isCorrect;

    private LocalDateTime submittedAt;
}
