package com.neuroarena.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.neuroarena.model.Battle;
import java.util.Optional;

public interface BattleRepository extends JpaRepository<Battle, Long> {
    Optional<Battle> findByRoomCode(String roomCode);
}