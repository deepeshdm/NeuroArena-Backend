package com.neuroarena.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "battle_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleQuestion {

    @Id
    @Column(length = 36)
    private String battleQuestionId;  // Changed from Long to String (UUID)

    @Column(name = "battle_id", nullable = false, length = 36)
    private String battleId;  // Changed from Battle object to String ID

    @Column(name = "question_id", nullable = false, length = 36)
    private String questionId;  // Changed from Question object to String ID

    private Integer questionNumber;

    private Integer pointsPossible;
}
