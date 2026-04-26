// RoomTypeController.java
package com.neuroarena.controller;

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
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {
        return ResponseEntity.ok(roomTypeRepository.findAll());
    }

    // Get room type by ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomType> getRoomTypeById(@PathVariable Integer id) {
        return roomTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}