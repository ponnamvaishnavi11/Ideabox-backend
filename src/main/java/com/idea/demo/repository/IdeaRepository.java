package com.idea.demo.repository;

import com.idea.demo.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Idea> findAllByOrderByCreatedAtDesc();
}