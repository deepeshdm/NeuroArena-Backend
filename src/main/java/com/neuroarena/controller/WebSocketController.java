package com.neuroarena.controller;

import com.neuroarena.model.Battle;
import com.neuroarena.model.BattlePlayer;
import com.neuroarena.repository.BattlePlayerRepository;
import com.neuroarena.repository.BattleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BattleRepository battleRepository;
    private final BattlePlayerRepository battlePlayerRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @MessageMapping("/quiz/join")
    public void joinRoom(@Payload Map<String, String> message) {
        
        String roomCode = message.get("roomCode");
        String username = message.get("username");
        
        log.info("WebSocket join: {} joining room {}", username, roomCode);
        
        // Find battle by room code
        Battle battle = battleRepository.findByRoomCode(roomCode).orElse(null);
        if (battle == null) {
            log.error("Room not found: {}", roomCode);
            return;
        }
        
        // Find player by username and battle
        Optional<BattlePlayer> playerOpt = battlePlayerRepository.findByBattleIdAndUsername(
            battle.getBattleId(), username);
        
        if (playerOpt.isEmpty()) {
            log.error("Player not found in room: {} - {}", roomCode, username);
            return;
        }
        
        BattlePlayer player = playerOpt.get();
        String avatarUrl = player.getAvatarIconUrl();
        
        // Get all players in this room
        List<BattlePlayer> players = battlePlayerRepository.findByBattleId(battle.getBattleId());
        
        // Build player list
        List<Map<String, Object>> playerList = players.stream().map(p -> {
            Map<String, Object> playerMap = new HashMap<>();
            playerMap.put("username", p.getUsername());
            playerMap.put("avatarIconUrl", p.getAvatarIconUrl());
            playerMap.put("status", p.getStatus());  // ← Include status
            return playerMap;
        }).toList();
        
        // Broadcast player list
        Map<String, Object> playerListMessage = new HashMap<>();
        playerListMessage.put("type", "PLAYER_LIST");
        playerListMessage.put("players", playerList);
        playerListMessage.put("count", playerList.size());
        playerListMessage.put("maxPlayers", 10);
        
        
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomCode,
            playerListMessage
        );
        
        // Broadcast system message: Player joined
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("type", "CHAT_MESSAGE");
        systemMessage.put("messageType", "SYSTEM");
        systemMessage.put("username", "SYSTEM");
        systemMessage.put("text", username + " joined the lobby");
        systemMessage.put("time", LocalDateTime.now().format(TIME_FORMATTER));
        
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomCode,
            systemMessage
        );
    }

    @MessageMapping("/quiz/chat")
    public void sendChatMessage(@Payload Map<String, String> message) {
        
        String roomCode = message.get("roomCode");
        String username = message.get("username");
        String text = message.get("text");
        
        log.info("Chat message in room {} from {}: {}", roomCode, username, text);
        
        // Find battle by room code
        Battle battle = battleRepository.findByRoomCode(roomCode).orElse(null);
        if (battle == null) {
            log.error("Room not found: {}", roomCode);
            return;
        }
        
        // Find player to get avatar
        Optional<BattlePlayer> playerOpt = battlePlayerRepository.findByBattleIdAndUsername(
            battle.getBattleId(), username);
        
        String avatarUrl = playerOpt.map(BattlePlayer::getAvatarIconUrl).orElse(null);
        
        // Broadcast chat message
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("type", "CHAT_MESSAGE");
        chatMessage.put("messageType", "USER");
        chatMessage.put("username", username);
        chatMessage.put("avatarUrl", avatarUrl);
        chatMessage.put("text", text);
        chatMessage.put("time", LocalDateTime.now().format(TIME_FORMATTER));
        
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomCode,
            chatMessage
        );
    }



    @MessageMapping("/quiz/ready")
    public void playerReady(@Payload Map<String, String> message) {
        
        String roomCode = message.get("roomCode");
        String username = message.get("username");
        
        log.info("Player {} is ready in room {}", username, roomCode);
        
        // Find battle
        Battle battle = battleRepository.findByRoomCode(roomCode).orElse(null);
        if (battle == null) {
            log.error("Room not found: {}", roomCode);
            return;
        }
        
        // Update player status to READY
        battlePlayerRepository.updatePlayerStatus(
            battle.getBattleId(), 
            username, 
            "READY"
        );
        
        // Get updated player list with status
        List<BattlePlayer> players = battlePlayerRepository.findByBattleId(battle.getBattleId());
        
        // Build player list with status
        List<Map<String, Object>> playerList = players.stream().map(p -> {
            Map<String, Object> playerMap = new HashMap<>();
            playerMap.put("username", p.getUsername());
            playerMap.put("avatarIconUrl", p.getAvatarIconUrl());
            playerMap.put("status", p.getStatus());  // ← Include status
            return playerMap;
        }).toList();
        
        // Broadcast updated player list
        Map<String, Object> playerListMessage = new HashMap<>();
        playerListMessage.put("type", "PLAYER_LIST");
        playerListMessage.put("players", playerList);
        playerListMessage.put("count", playerList.size());
        playerListMessage.put("maxPlayers", 10);
        
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomCode,
            playerListMessage
        );
        
        // Broadcast system message
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("type", "CHAT_MESSAGE");
        systemMessage.put("messageType", "SYSTEM");
        systemMessage.put("username", "SYSTEM");
        systemMessage.put("text", username + " is ready for battle!");
        systemMessage.put("time", LocalDateTime.now().format(TIME_FORMATTER));
        
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomCode,
            systemMessage
        );
        
        // Check if all players are ready AND room is full
        long readyCount = battlePlayerRepository.countReadyPlayers(battle.getBattleId());
        long totalPlayers = battlePlayerRepository.countByBattleId(battle.getBattleId());
        
        if (readyCount == totalPlayers && totalPlayers == 10) {
            // All players ready and room full - start battle
            startBattle(battle, roomCode);
        }
    }
    
    private void startBattle(Battle battle, String roomCode) {
        // Update all player statuses to IN_GAME
        battlePlayerRepository.updatePlayerStatus(battle.getBattleId(), null, "IN_GAME");
        
        Map<String, Object> startMessage = new HashMap<>();
        startMessage.put("type", "BATTLE_START");
        startMessage.put("message", "All players ready! Battle starting...");
        startMessage.put("battleId", battle.getBattleId());
        
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomCode,
            startMessage
        );
        
        log.info("Battle starting in room: {}", roomCode);
    }




    @MessageMapping("/quiz/leave")
    public void leaveRoom(@Payload Map<String, String> message) {
        
        String roomCode = message.get("roomCode");
        String username = message.get("username");
        
        log.info("Player {} leaving room {}", username, roomCode);
        
        // Find battle
        Battle battle = battleRepository.findByRoomCode(roomCode).orElse(null);
        if (battle == null) {
            log.error("Room not found: {}", roomCode);
            return;
        }
        
        // Remove player from database
        battlePlayerRepository.deleteByBattleIdAndUsername(battle.getBattleId(), username);
        
        // Get remaining players
        List<BattlePlayer> remainingPlayers = battlePlayerRepository.findByBattleId(battle.getBattleId());
        
        // Build updated player list with status
        List<Map<String, Object>> playerList = remainingPlayers.stream().map(p -> {
            Map<String, Object> playerMap = new HashMap<>();
            playerMap.put("username", p.getUsername());
            playerMap.put("avatarIconUrl", p.getAvatarIconUrl());
            playerMap.put("status", p.getStatus());
            return playerMap;
        }).toList();
        
        // Broadcast updated player list to everyone in room
        Map<String, Object> playerListMessage = new HashMap<>();
        playerListMessage.put("type", "PLAYER_LIST");
        playerListMessage.put("players", playerList);
        playerListMessage.put("count", playerList.size());
        playerListMessage.put("maxPlayers", 10);
        
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomCode,
            playerListMessage
        );
        
        // Broadcast system message that player left
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("type", "CHAT_MESSAGE");
        systemMessage.put("messageType", "SYSTEM");
        systemMessage.put("username", "SYSTEM");
        systemMessage.put("text", username + " left the lobby");
        systemMessage.put("time", LocalDateTime.now().format(TIME_FORMATTER));
        
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomCode,
            systemMessage
        );
    }


}