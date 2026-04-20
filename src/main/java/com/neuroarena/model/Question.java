package com.neuroarena.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String difficulty;

    private Integer basePoints;
    private Integer timeLimitSeconds;

    private String category;

    private LocalDateTime createdAt;
}
