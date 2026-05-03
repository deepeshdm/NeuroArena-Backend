// ConfigController.java
package com.neuroarena.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${game.max-players}")
    private int maxPlayers;

    @Value("${game.questions-per-battle}")
    private int questionsPerBattle;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("maxPlayers", maxPlayers);
        config.put("questionsPerBattle", questionsPerBattle);

        return ResponseEntity.ok(config);
    }
}