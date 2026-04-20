package com.neuroarena.repository;

import com.neuroarena.model.BattleQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BattleQuestionRepository extends JpaRepository<BattleQuestion, Long> {

    List<BattleQuestion> findByBattle_BattleIdOrderByQuestionNumber(Long battleId);

}
