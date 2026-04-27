package com.neuroarena.controller;

import com.neuroarena.dto.ApiResponse;
import com.neuroarena.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Value("${game.max-players}")
    private int MAX_PLAYERS;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<RoomService.JoinResponse>> joinOrCreateRoom(
            @RequestBody Map<String, Integer> request) {
        
        Integer roomTypeId = request.get("roomTypeId");
        
        // Validate roomTypeId
        if (roomTypeId == null || roomTypeId < 1 || roomTypeId > 8) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid room type. Must be between 1-8"));
        }
        
        try {
            RoomService.JoinResponse response = roomService.joinOrCreateRoom(roomTypeId);
            return ResponseEntity.ok(ApiResponse.success(response, "Room joined successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to join room: " + e.getMessage()));
        }
    }

    @PostMapping("/join-by-code")
    public ResponseEntity<ApiResponse<RoomService.JoinResponse>> joinRoomByCode(

        @RequestBody Map<String, String> request) {
        
        String roomCode = request.get("roomCode");
        String username = request.get("username");
        
        // Validate roomCode
        if (roomCode == null || roomCode.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Room code is required"));
        }
        
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username is required"));
        }
        
        if (username.length() < 3 || username.length() > 20) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username must be 3-20 characters"));
        }
        
        // Call service
        try {
            RoomService.JoinResponse response = roomService.joinRoomByCode(
                roomCode.trim(), 
                username.trim()
            );
            return ResponseEntity.ok(ApiResponse.success(response, "Joined room successfully"));
            
        } catch (RuntimeException e) {
            String error = e.getMessage();
            
            if (error.equals("ROOM_NOT_FOUND")) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("Room not found with code: " + roomCode));
            } else if (error.equals("ROOM_ALREADY_STARTED")) {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error("Battle already in progress. Cannot join."));
            } else if (error.equals("ROOM_FULL")) {
                return ResponseEntity.status(400)
                        .body(ApiResponse.error("Room is full"));
            } else {
                return ResponseEntity.internalServerError()
                        .body(ApiResponse.error("Failed to join room: " + error));
            }
        }
    }

}