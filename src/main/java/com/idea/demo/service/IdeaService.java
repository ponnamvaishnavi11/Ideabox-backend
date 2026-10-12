package com.idea.demo.service;

import com.idea.demo.dto.IdeaRequest;
import com.idea.demo.entity.Idea;
import com.idea.demo.entity.IdeaStatus;
import com.idea.demo.entity.IdeaType;
import com.idea.demo.entity.User;
import com.idea.demo.repository.IdeaRepository;
import com.idea.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdeaService {

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

    private void checkOwner(Idea idea, User user) {
        if (!idea.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only change your own ideas");
        }
    }

    private boolean contains(String field, String query) {
        return field != null && field.toLowerCase().contains(query.toLowerCase());
    }

    public Idea create(IdeaRequest request, String email) {
        User owner = getUser(email);

        Idea idea = new Idea();
        idea.setTitle(request.getTitle());
        idea.setDescription(request.getDescription());
        idea.setTags(request.getTags());
        idea.setSkillsNeeded(request.getSkillsNeeded());
        idea.setType(request.getType());
        idea.setStatus(IdeaStatus.OPEN);
        idea.setOwner(owner);

        return ideaRepository.save(idea);
    }

    public List<Idea> getAll(String keyword, IdeaType type, IdeaStatus status, String tag, String skill) {
        return ideaRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(i -> keyword == null || keyword.isBlank()
                        || contains(i.getTitle(), keyword)
                        || contains(i.getDescription(), keyword))
                .filter(i -> type == null || i.getType() == type)
                .filter(i -> status == null || i.getStatus() == status)
                .filter(i -> tag == null || tag.isBlank() || contains(i.getTags(), tag))
                .filter(i -> skill == null || skill.isBlank() || contains(i.getSkillsNeeded(), skill))
                .toList();
    }

    public Idea getById(Long id) {
        return getIdea(id);
    }

    public List<Idea> getMyIdeas(String email) {
        User user = getUser(email);
        return ideaRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId());
    }

    public Idea update(Long id, IdeaRequest request, String email) {
        User user = getUser(email);
        Idea idea = getIdea(id);
        checkOwner(idea, user);

        idea.setTitle(request.getTitle());
        idea.setDescription(request.getDescription());
        idea.setTags(request.getTags());
        idea.setSkillsNeeded(request.getSkillsNeeded());
        idea.setType(request.getType());
        if (request.getStatus() != null) {
            idea.setStatus(request.getStatus());
        }

        return ideaRepository.save(idea);
    }

    public void delete(Long id, String email) {
        User user = getUser(email);
        Idea idea = getIdea(id);
        checkOwner(idea, user);
        ideaRepository.delete(idea);
    }
}