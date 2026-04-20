package com.neuroarena.repository;

import com.neuroarena.model.BattlePlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BattlePlayerRepository extends JpaRepository<BattlePlayer, Long> {

    List<BattlePlayer> findByBattle_BattleId(Long battleId);

    boolean existsByBattle_BattleIdAndUsername(Long battleId, String username);
}