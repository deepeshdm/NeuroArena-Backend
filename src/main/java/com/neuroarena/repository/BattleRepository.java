package com.neuroarena.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.neuroarena.model.Battle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BattleRepository extends JpaRepository<Battle, String> {  // String ID

    // Find by room code
    Optional<Battle> findByRoomCode(String roomCode);

    // Find active battles (WAITING or IN_PROGRESS)
    List<Battle> findByStatusIn(List<String> statuses);
    
    List<Battle> findByStatus(String status);

    // Find waiting battles older than specified time (for cleanup)
    List<Battle> findByStatusAndCreatedAtBefore(String status, LocalDateTime time);

    // Update battle status
    @Modifying
    @Query("UPDATE Battle b SET b.status = :status, b.startedAt = :startedAt WHERE b.battleId = :battleId")
    void updateBattleStatus(@Param("battleId") String battleId, 
                           @Param("status") String status, 
                           @Param("startedAt") LocalDateTime startedAt);

    // End battle
    @Modifying
    @Query("UPDATE Battle b SET b.status = 'ENDED', b.endedAt = :endedAt WHERE b.battleId = :battleId")
    void endBattle(@Param("battleId") String battleId, @Param("endedAt") LocalDateTime endedAt);


    @Modifying
    @Transactional
    @Query("UPDATE Battle b SET b.status = 'IN_PROGRESS' WHERE b.battleId = :battleId")
    void updateStatusToInProgress(String battleId);


}