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
    @Column(length = 36)
    private String questionId;  // Changed from Long to String (UUID)

    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;  // Changed from RoomType object to Integer ID

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String difficulty;

    private Integer basePoints;
    private Integer timeLimitSeconds;

    private String category;

    private LocalDateTime createdAt;
}
