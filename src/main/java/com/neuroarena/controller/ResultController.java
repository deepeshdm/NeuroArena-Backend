package com.neuroarena.controller;

import com.neuroarena.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/result")
@RequiredArgsConstructor
public class ResultController {

    private final BattleService battleService;

    @GetMapping("/final_result")
    public ResponseEntity<Map<String, Object>> getResult(
            @RequestParam String battleId,
            @RequestParam String playerId) {
        Map<String, Object> result = battleService.getResultData(battleId, playerId);
        return ResponseEntity.ok(result);
    }
}