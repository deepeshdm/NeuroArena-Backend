package com.neuroarena.controller;

import com.neuroarena.dto.ApiResponse;
import com.neuroarena.model.RoomType;
import com.neuroarena.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeRepository roomTypeRepository;

    // Get all room types
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomType>>> getAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(roomTypes, "Room types fetched successfully"));
    }

    // Get room type by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomType>> getRoomTypeById(@PathVariable Integer id) {
        return roomTypeRepository.findById(id)
                .map(roomType -> ResponseEntity.ok(ApiResponse.success(roomType, "Room type found")))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Room type not found with id: " + id)));
    }
}