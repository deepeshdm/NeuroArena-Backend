package com.neuroarena.repository;

import com.neuroarena.model.BattlePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;

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


    // Update single player status by username
    @Modifying
    @Transactional
    @Query("UPDATE BattlePlayer bp SET bp.status = :status WHERE bp.battleId = :battleId AND bp.username = :username")
    void updatePlayerStatus(@Param("battleId") String battleId, 
                           @Param("username") String username, 
                           @Param("status") String status);
    
    // Update all players in a battle (for start of game)
    @Modifying
    @Transactional
    @Query("UPDATE BattlePlayer bp SET bp.status = :status WHERE bp.battleId = :battleId")
    void updateAllPlayerStatus(@Param("battleId") String battleId, 
                              @Param("status") String status);
    
    // Count players with READY status in a battle
    @Query("SELECT COUNT(bp) FROM BattlePlayer bp WHERE bp.battleId = :battleId AND bp.status = 'READY'")
    long countReadyPlayers(@Param("battleId") String battleId);
    
    // Get players by status
    List<BattlePlayer> findByBattleIdAndStatus(String battleId, String status);

}