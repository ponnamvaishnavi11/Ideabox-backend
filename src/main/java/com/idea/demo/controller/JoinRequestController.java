package com.idea.demo.controller;

import com.idea.demo.entity.JoinRequest;
import com.idea.demo.entity.User;
import com.idea.demo.service.JoinRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JoinRequestController {

    private final JoinRequestService requestService;

    @PostMapping("/api/ideas/{ideaId}/join")
    public ResponseEntity<JoinRequest> join(@PathVariable Long ideaId, Authentication auth) {
        return ResponseEntity.ok(requestService.sendRequest(ideaId, auth.getName()));
    }

    @GetMapping("/api/ideas/{ideaId}/requests")
    public ResponseEntity<List<JoinRequest>> requestsForIdea(@PathVariable Long ideaId, Authentication auth) {
        return ResponseEntity.ok(requestService.getRequestsForIdea(ideaId, auth.getName()));
    }

    @GetMapping("/api/ideas/{ideaId}/team")
    public ResponseEntity<List<User>> team(@PathVariable Long ideaId) {
        return ResponseEntity.ok(requestService.getTeam(ideaId));
    }

    @GetMapping("/api/requests/my")
    public ResponseEntity<List<JoinRequest>> myRequests(Authentication auth) {
        return ResponseEntity.ok(requestService.getMyRequests(auth.getName()));
    }

    @PutMapping("/api/requests/{requestId}/accept")
    public ResponseEntity<JoinRequest> accept(@PathVariable Long requestId, Authentication auth) {
        return ResponseEntity.ok(requestService.accept(requestId, auth.getName()));
    }

    @PutMapping("/api/requests/{requestId}/reject")
    public ResponseEntity<JoinRequest> reject(@PathVariable Long requestId, Authentication auth) {
        return ResponseEntity.ok(requestService.reject(requestId, auth.getName()));
    }
}