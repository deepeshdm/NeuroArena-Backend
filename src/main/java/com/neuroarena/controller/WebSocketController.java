package com.neuroarena.controller;

import com.neuroarena.model.Battle;
import com.neuroarena.model.BattlePlayer;
import com.neuroarena.repository.BattlePlayerRepository;
import com.neuroarena.repository.BattleRepository;
import com.neuroarena.service.BattleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;

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
    private final BattleService battleService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Value("${game.max-players}")
    private int MAX_PLAYERS;

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
        playerListMessage.put("maxPlayers", MAX_PLAYERS);
        
        
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
        playerListMessage.put("maxPlayers", MAX_PLAYERS);
        
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
        
        if (readyCount == totalPlayers && totalPlayers == MAX_PLAYERS) {
            // All players ready and room full - start battle
            startBattle(battle, roomCode);
        }
    }

    private void startBattle(Battle battle, String roomCode) {

        // Call BattleService to handle the battle start logic
        battleService.startBattle(battle.getBattleId(), battle.getRoomTypeId());

        // Broadcast to all players
        Map<String, Object> startMessage = new HashMap<>();
        startMessage.put("type", "BATTLE_START");
        startMessage.put("battleId", battle.getBattleId());
        startMessage.put("roomTypeId", battle.getRoomTypeId());
        startMessage.put("message", "Battle starting!");
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, startMessage);


        // broadcast initial leaderboard with all players at 0
        List<Map<String, Object>> leaderboard = battleService.getLeaderboard(battle.getBattleId());
        Map<String, Object> lbMsg = new HashMap<>();
        lbMsg.put("type", "LEADERBOARD_UPDATE");
        lbMsg.put("leaderboard", leaderboard);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, lbMsg);

        log.info("Battle started in room: {}", roomCode);
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
        playerListMessage.put("maxPlayers", MAX_PLAYERS);
        
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


    private void sendErrorToPlayer(String playerId, String errorMessage) {
        Map<String, Object> errorMsg = new HashMap<>();
        errorMsg.put("type", "ERROR");
        errorMsg.put("message", errorMessage);
        messagingTemplate.convertAndSend("/topic/player/"+playerId, errorMsg);
    }

    @MessageMapping("/quiz/get-question")
    public void getCurrentQuestion(@Payload Map<String, String> message) {

        String battleId = message.get("battleId");
        String playerId = message.get("playerId");

        log.info("Player {} requesting next question for battle {}", playerId, battleId);

        // 1. Validate battle exists
        Battle battle = battleRepository.findById(battleId).orElse(null);
        if (battle == null) {
            sendErrorToPlayer(playerId, "Battle not found");
            return;
        }

        // 2. Validate battle is IN_PROGRESS
        if (!"IN_PROGRESS".equals(battle.getStatus())) {
            sendErrorToPlayer(playerId, "Battle is not active");
            return;
        }

        // 3. Get next unanswered question
        Map<String, Object> question = battleService.getNextUnansweredQuestion(battleId, playerId);

        // 4. If no question, player has completed their questions
        if (question == null) {

            // Tell THIS player to wait
            Map<String, Object> waitMsg = new HashMap<>();
            waitMsg.put("type", "WAITING_FOR_PLAYERS");
            waitMsg.put("message", "Waiting for other players to finish...");
            messagingTemplate.convertAndSend("/topic/player/" + playerId, waitMsg);

            // Check if ALL players have now finished
            if (battleService.haveAllPlayersCompleted(battleId)) {

                // Send result to each player individually
                List<BattlePlayer> players = battlePlayerRepository.findByBattleId(battleId);
                for (BattlePlayer bp : players) {
                    Map<String, Object> resultData = battleService.getResultData(battleId, bp.getPlayerId());
                    resultData.put("type", "BATTLE_COMPLETED");
                    messagingTemplate.convertAndSend("/topic/player/" + bp.getPlayerId(), resultData);
                }

                log.info("All players completed battle {}, results sent", battleId);
            }

            return;
        }

        // 5. Send question to player (no DTO, just raw map)
        Map<String, Object> questionMsg = new HashMap<>();
        questionMsg.put("type", "QUESTION");
        questionMsg.put("data", question);
        messagingTemplate.convertAndSend( "/topic/player/"+playerId, questionMsg);

        log.info("Sent question {} to player {}", question.get("questionNumber"), playerId);
    }

    @MessageMapping("/quiz/submit-answer")
    public void submitAnswer(@Payload Map<String, String> message) {

        String battleId        = message.get("battleId");
        String playerId        = message.get("playerId");
        String questionId      = message.get("questionId");
        String selectedAnswerId = message.get("selectedAnswerId"); // null = timed out
        int responseTimeMs     = Integer.parseInt(message.getOrDefault("responseTimeMs", "30000"));

        log.info("Player {} submitting answer for battle {}, question {}", playerId, battleId, questionId);

        // 1. Process & score the answer
        Map<String, Object> result = battleService.submitAnswer(
                battleId, playerId, questionId, selectedAnswerId, responseTimeMs
        );

        // 2. Send result back to THIS player only
        result.put("type", "ANSWER_RESULT");
        messagingTemplate.convertAndSend("/topic/player/" + playerId, result);

        // 3. Broadcast updated leaderboard to ALL players in the room
        Battle battle = battleRepository.findById(battleId).orElse(null);
        if (battle != null) {
            List<Map<String, Object>> leaderboard = battleService.getLeaderboard(battleId);
            Map<String, Object> lbMsg = new HashMap<>();
            lbMsg.put("type", "LEADERBOARD_UPDATE");
            lbMsg.put("leaderboard", leaderboard);
            messagingTemplate.convertAndSend("/topic/room/" + battle.getRoomCode(), lbMsg);
        }
    }



}