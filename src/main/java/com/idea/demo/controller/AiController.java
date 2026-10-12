package com.idea.demo.controller;

import com.idea.demo.dto.AiIdeaResponse;
import com.idea.demo.dto.AiRequest;
import com.idea.demo.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/improve-idea")
    public ResponseEntity<AiIdeaResponse> improve(@Valid @RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.improve(request.getRoughText()));
    }
}