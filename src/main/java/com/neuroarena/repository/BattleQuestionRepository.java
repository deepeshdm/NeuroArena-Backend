package com.neuroarena.repository;

import com.neuroarena.model.BattleQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BattleQuestionRepository extends JpaRepository<BattleQuestion, String> {  // String ID

    // Find by battle ID sorted by question number
    List<BattleQuestion> findByBattleIdOrderByQuestionNumber(String battleId);

    // Find specific question in battle
    Optional<BattleQuestion> findByBattleIdAndQuestionNumber(String battleId, Integer questionNumber);

    // Delete all questions for a battle (cleanup)
    void deleteByBattleId(String battleId);
}