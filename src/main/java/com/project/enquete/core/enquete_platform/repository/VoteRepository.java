package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
}
