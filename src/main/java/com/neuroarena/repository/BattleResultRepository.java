package com.neuroarena.repository;

import com.neuroarena.model.BattleResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BattleResultRepository extends JpaRepository<BattleResult, Long> {

    List<BattleResult> findByBattle_BattleIdOrderByFinalRankAsc(Long battleId);

}
