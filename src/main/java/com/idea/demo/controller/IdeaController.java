package com.idea.demo.controller;

import com.idea.demo.dto.IdeaRequest;
import com.idea.demo.entity.Idea;
import com.idea.demo.entity.IdeaStatus;
import com.idea.demo.entity.IdeaType;
import com.idea.demo.service.IdeaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ideas")
@RequiredArgsConstructor
public class IdeaController {

    private final IdeaService ideaService;

    @PostMapping
    public ResponseEntity<Idea> create(@Valid @RequestBody IdeaRequest request,
                                       Authentication auth) {
        return ResponseEntity.ok(ideaService.create(request, auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Idea>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) IdeaType type,
            @RequestParam(required = false) IdeaStatus status,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String skill) {
        return ResponseEntity.ok(ideaService.getAll(keyword, type, status, tag, skill));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Idea>> getMyIdeas(Authentication auth) {
        return ResponseEntity.ok(ideaService.getMyIdeas(auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idea> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ideaService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Idea> update(@PathVariable Long id,
                                       @Valid @RequestBody IdeaRequest request,
                                       Authentication auth) {
        return ResponseEntity.ok(ideaService.update(id, request, auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id,
                                                       Authentication auth) {
        ideaService.delete(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "Idea deleted"));
    }
}