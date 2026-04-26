package com.neuroarena.controller;

import com.neuroarena.dto.ApiResponse;
import com.neuroarena.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

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
}