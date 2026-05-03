package com.neuroarena.repository;

import com.neuroarena.model.PlayerAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PlayerAnswerRepository extends JpaRepository<PlayerAnswer, String> {  // String ID

    // Find all answers for a battle
    List<PlayerAnswer> findByBattleId(String battleId);

    // Find answers by player and battle
    List<PlayerAnswer> findByPlayerIdAndBattleId(String playerId, String battleId);

    // Find answer for specific question in battle
    Optional<PlayerAnswer> findByBattleIdAndPlayerIdAndQuestionNumber(String battleId, String playerId, Integer questionNumber);

    // Calculate total score for a player in a battle
    @Query("SELECT COALESCE(SUM(pa.pointsEarned), 0) FROM PlayerAnswer pa WHERE pa.battleId = :battleId AND pa.playerId = :playerId")
    Integer calculateTotalScore(@Param("battleId") String battleId, @Param("playerId") String playerId);

    // Calculate correct answer count for a player in a battle
    @Query("SELECT COUNT(pa) FROM PlayerAnswer pa WHERE pa.battleId = :battleId AND pa.playerId = :playerId AND pa.isCorrect = true")
    Integer calculateCorrectCount(@Param("battleId") String battleId, @Param("playerId") String playerId);

    // Calculate average response time for a player in a battle
    @Query("SELECT COALESCE(AVG(pa.responseTimeMs), 0) FROM PlayerAnswer pa WHERE pa.battleId = :battleId AND pa.playerId = :playerId")
    Double calculateAverageResponseTime(@Param("battleId") String battleId, @Param("playerId") String playerId);

    // Get all question numbers that a player has already answered in this battle
    @Query("SELECT pa.questionNumber FROM PlayerAnswer pa WHERE pa.battleId = :battleId AND pa.playerId = :playerId")
    Set<Integer> findAnsweredQuestionNumbers(@Param("battleId") String battleId,
                                             @Param("playerId") String playerId);

    // Delete answers for a battle (cleanup)
    void deleteByBattleId(String battleId);

    boolean existsByBattleIdAndPlayerIdAndQuestionId(String battleId, String playerId, String questionId);

    @Query("SELECT COALESCE(SUM(pa.pointsEarned), 0) FROM PlayerAnswer pa " +
            "WHERE pa.battleId = :battleId AND pa.playerId = :playerId")
    int sumPointsByBattleIdAndPlayerId(String battleId, String playerId);

    List<PlayerAnswer> findByBattleIdAndPlayerId(String battleId, String playerId);
}