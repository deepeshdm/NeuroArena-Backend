package com.neuroarena.repository;

import com.neuroarena.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    // Count questions by room type ID
    long countByRoomTypeId(Integer roomTypeId);

    // Find random questions for a room type with dynamic limit
    @Query(value = "SELECT * FROM questions WHERE room_type_id = :roomTypeId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByRoomType(@Param("roomTypeId") Integer roomTypeId,
                                                 @Param("limit") Integer questionsPerBattle);

    // Find random questions for Mixed Bag (all room types) with dynamic limit
    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomMixedQuestions(@Param("limit") Integer questionsPerBattle);
}