package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {
}
