package com.neuroarena.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @Column(length = 36)
    private String answerId;  // Changed from Long to String (UUID)

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String answerText;
    private Boolean isCorrect;

    private Integer displayOrder;
}
