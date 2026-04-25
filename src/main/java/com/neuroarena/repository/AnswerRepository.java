package com.neuroarena.repository;

import com.neuroarena.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, String> {  // String ID
    
    List<Answer> findByQuestion_QuestionId(String questionId);
}