package com.neuroarena.repository;

import com.neuroarena.model.PlayerAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerAnswerRepository extends JpaRepository<PlayerAnswer, Long> {

    List<PlayerAnswer> findByBattle_BattleId(Long battleId);

    List<PlayerAnswer> findByPlayerIdAndBattle_BattleId(Long playerId, Long battleId);
}
