package com.neuroarena.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {

    @Id
    private Integer roomTypeId;  // Changed from Long to Integer (TINYINT in DB)

    private String name;
    private String description;
    private String difficultyLevel;
    private String iconPath;

    private Integer basePoints;

    // getters & setters
}
