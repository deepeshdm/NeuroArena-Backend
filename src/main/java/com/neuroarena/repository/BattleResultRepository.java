package com.neuroarena.repository;

import com.neuroarena.model.BattleResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BattleResultRepository extends JpaRepository<BattleResult, String> {  // String ID

    // Find results by battle ID ordered by rank
    List<BattleResult> findByBattleIdOrderByFinalRankAsc(String battleId);

    // Find specific player's result in battle
    Optional<BattleResult> findByBattleIdAndPlayerId(String battleId, String playerId);

    // Delete results for a battle (cleanup)
    void deleteByBattleId(String battleId);

    // Get winner of battle
    @Query("SELECT br FROM BattleResult br WHERE br.battleId = :battleId ORDER BY br.finalRank ASC LIMIT 1")
    Optional<BattleResult> findWinnerByBattleId(@Param("battleId") String battleId);
}