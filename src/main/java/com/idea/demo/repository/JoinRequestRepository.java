package com.idea.demo.repository;

import com.idea.demo.entity.JoinRequest;
import com.idea.demo.entity.JoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {

    boolean existsByIdeaIdAndRequesterId(Long ideaId, Long requesterId);

    List<JoinRequest> findByIdeaIdOrderByCreatedAtDesc(Long ideaId);

    List<JoinRequest> findByRequesterIdOrderByCreatedAtDesc(Long requesterId);

    List<JoinRequest> findByIdeaIdAndStatus(Long ideaId, JoinRequestStatus status);
}