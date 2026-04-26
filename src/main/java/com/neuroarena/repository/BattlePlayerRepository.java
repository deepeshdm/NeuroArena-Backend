package com.neuroarena.repository;

import com.neuroarena.model.BattlePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BattlePlayerRepository extends JpaRepository<BattlePlayer, String> {

    // Find by battle ID
    List<BattlePlayer> findByBattleId(String battleId);

    // Check if username exists in battle
    boolean existsByBattleIdAndUsername(String battleId, String username);

    // Count players in a battle
    long countByBattleId(String battleId);

    // Find player by battle ID and player ID - THIS WAS CAUSING THE ERROR
    Optional<BattlePlayer> findByBattleIdAndPlayerId(String battleId, String playerId);

    // Delete all players for a battle
    void deleteByBattleId(String battleId);

    Optional<BattlePlayer> findByBattleIdAndUsername(String battleId, String username);
}