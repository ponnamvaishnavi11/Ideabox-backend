package com.idea.demo.service;

import com.idea.demo.entity.*;
import com.idea.demo.repository.IdeaRepository;
import com.idea.demo.repository.JoinRequestRepository;
import com.idea.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinRequestService {

    private final JoinRequestRepository requestRepository;
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Idea getIdea(Long id) {
        return ideaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Idea not found"));
    }

    private JoinRequest getRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
    }

    private void checkOwner(Idea idea, User user) {
        if (!idea.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the idea owner can do this");
        }
    }

    public JoinRequest sendRequest(Long ideaId, String email) {
        User user = getUser(email);
        Idea idea = getIdea(ideaId);

        if (idea.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot join your own idea");
        }
        if (idea.getStatus() == IdeaStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This idea is already completed");
        }
        if (requestRepository.existsByIdeaIdAndRequesterId(ideaId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already sent a request for this idea");
        }

        JoinRequest request = new JoinRequest();
        request.setIdea(idea);
        request.setRequester(user);
        request.setStatus(JoinRequestStatus.PENDING);
        return requestRepository.save(request);
    }

    public List<JoinRequest> getRequestsForIdea(Long ideaId, String email) {
        User user = getUser(email);
        Idea idea = getIdea(ideaId);
        checkOwner(idea, user);
        return requestRepository.findByIdeaIdOrderByCreatedAtDesc(ideaId);
    }

    public List<JoinRequest> getMyRequests(String email) {
        User user = getUser(email);
        return requestRepository.findByRequesterIdOrderByCreatedAtDesc(user.getId());
    }

    public List<User> getTeam(Long ideaId) {
        getIdea(ideaId);
        return requestRepository.findByIdeaIdAndStatus(ideaId, JoinRequestStatus.ACCEPTED)
                .stream()
                .map(JoinRequest::getRequester)
                .toList();
    }

    public JoinRequest accept(Long requestId, String email) {
        return decide(requestId, email, JoinRequestStatus.ACCEPTED);
    }

    public JoinRequest reject(Long requestId, String email) {
        return decide(requestId, email, JoinRequestStatus.REJECTED);
    }

    private JoinRequest decide(Long requestId, String email, JoinRequestStatus newStatus) {
        User user = getUser(email);
        JoinRequest request = getRequest(requestId);
        Idea idea = request.getIdea();
        checkOwner(idea, user);

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This request is already " + request.getStatus());
        }

        request.setStatus(newStatus);

        if (newStatus == JoinRequestStatus.ACCEPTED && idea.getStatus() == IdeaStatus.OPEN) {
            idea.setStatus(IdeaStatus.IN_PROGRESS);
            ideaRepository.save(idea);
        }

        return requestRepository.save(request);
    }
}