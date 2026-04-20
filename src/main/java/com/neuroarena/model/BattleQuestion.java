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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long battleQuestionId;

    @ManyToOne
    @JoinColumn(name = "battle_id")
    private Battle battle;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private Integer questionNumber;

    private Integer pointsPossible;
}
