package com.neuroarena.service;

import com.neuroarena.model.Battle;
import com.neuroarena.model.BattlePlayer;
import com.neuroarena.repository.BattlePlayerRepository;
import com.neuroarena.repository.BattleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final BattleRepository battleRepository;
    private final BattlePlayerRepository battlePlayerRepository;
    private final AvatarService avatarService;

    private final Random random = new Random();

    @Transactional
    public JoinResponse joinOrCreateRoom(Integer roomTypeId) {
        
        // Look for existing waiting room with same type that has space
        List<Battle> waitingRooms = battleRepository.findByStatus("WAITING");
        Battle targetRoom = null;
        
        for (Battle room : waitingRooms) {
            if (room.getRoomTypeId().equals(roomTypeId)) {
                long playerCount = battlePlayerRepository.countByBattleId(room.getBattleId());
                if (playerCount < 10) {
                    targetRoom = room;
                    break;
                }
            }
        }
        
        // If no room found, create a new one
        if (targetRoom == null) {
            // Generate unique room code
            String roomCode;
            do {
                roomCode = String.format("%06d", random.nextInt(1000000));
            } while (battleRepository.findByRoomCode(roomCode).isPresent());
            
            // Get random host from AvatarService
            var alien = avatarService.getRandomAlienPlayer();
            String hostName = alien.username;
            String hostAvatar = alien.avatarIconUrl;
            
            String battleId = UUID.randomUUID().toString();
            String hostPlayerId = UUID.randomUUID().toString();
            
            // Create battle
            Battle newBattle = Battle.builder()
                    .battleId(battleId)
                    .roomCode(roomCode)
                    .roomTypeId(roomTypeId)
                    .hostUsername(hostName)
                    .status("WAITING")
                    .currentQuestionNumber(0)
                    .createdAt(LocalDateTime.now())
                    .pointsMultiplier(getMultiplier(roomTypeId))
                    .build();
            battleRepository.save(newBattle);
            
            // Create host player
            BattlePlayer host = BattlePlayer.builder()
                    .battlePlayerId(UUID.randomUUID().toString())
                    .battleId(battleId)
                    .playerId(hostPlayerId)
                    .username(hostName)
                    .avatarIconUrl(hostAvatar)
                    .joinedAt(LocalDateTime.now())
                    .build();
            battlePlayerRepository.save(host);
            
            log.info("Created new room: {} for type {}", roomCode, roomTypeId);
            
            // Return battle + player info together
            return new JoinResponse(newBattle, hostPlayerId, hostName, hostAvatar);
        }
        
        // Join existing room
        List<String> existingNames = battlePlayerRepository.findByBattleId(targetRoom.getBattleId())
                .stream()
                .map(BattlePlayer::getUsername)
                .toList();
        
        // Generate unique username
        String username;
        int attempts = 0;
        do {
            var alien = avatarService.getRandomAlienPlayer();
            username = alien.username + random.nextInt(1000);
            attempts++;
            if (attempts > 50) {
                username = "Alien" + System.currentTimeMillis();
                break;
            }
        } while (existingNames.contains(username));
        
        // Get random avatar
        var alien = avatarService.getRandomAlienPlayer();
        String avatarUrl = alien.avatarIconUrl;
        String playerId = UUID.randomUUID().toString();
        
        // Create new player
        BattlePlayer newPlayer = BattlePlayer.builder()
                .battlePlayerId(UUID.randomUUID().toString())
                .battleId(targetRoom.getBattleId())
                .playerId(playerId)
                .username(username)
                .avatarIconUrl(avatarUrl)
                .joinedAt(LocalDateTime.now())
                .build();
        battlePlayerRepository.save(newPlayer);
        
        log.info("Player {} joined room {}", username, targetRoom.getRoomCode());
        
        // Return battle + player info together
        return new JoinResponse(targetRoom, playerId, username, avatarUrl);
    }
    
    private double getMultiplier(int roomTypeId) {
        return switch (roomTypeId) {
            case 5 -> 1.5;
            case 6 -> 2.0;
            case 4 -> 0.8;
            default -> 1.0;
        };
    }
    
    // Simple wrapper class
    public static class JoinResponse {
        public Battle battle;
        public String playerId;
        public String username;
        public String avatarIconUrl;
        
        public JoinResponse(Battle battle, String playerId, String username, String avatarIconUrl) {
            this.battle = battle;
            this.playerId = playerId;
            this.username = username;
            this.avatarIconUrl = avatarIconUrl;
        }
    }
}