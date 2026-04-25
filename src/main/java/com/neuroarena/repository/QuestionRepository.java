package com.neuroarena.repository;

import com.neuroarena.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    
    // Count questions by room type ID
    long countByRoomTypeId(Integer roomTypeId);
    
    // Find random questions for a room type
    @Query(value = "SELECT * FROM questions WHERE room_type_id = ?1 ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Question> findRandomQuestionsByRoomType(Integer roomTypeId);
    
    // Find random questions for Mixed Bag (all room types)
    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Question> findRandomMixedQuestions();
}