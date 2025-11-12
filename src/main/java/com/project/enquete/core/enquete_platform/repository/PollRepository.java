package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {

    @Query(value = "SELECT p FROM Poll p JOIN FETCH p.options WHERE p.createdBy.id = :userId",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Poll p WHERE p.createdBy.id = :userId")
    Page<Poll> findAllWithOptionsForCurrentUser(@Param("userId") UUID userId, Pageable pageable);

    @Query(value = """
            SELECT * FROM Polls
            WHERE expires_at > NOW()
            AND visibility = 'public'
            ORDER BY RANDOM()
            LIMIT 6
            """, nativeQuery = true)
    List<Poll> findRandomPublicPolls();

    @Query(value = """
            SELECT *
            FROM (
                SELECT DISTINCT ON (p.id) p.*, v.voted_at
                FROM polls p
                JOIN options o ON p.id = o.poll_id
                JOIN votes v ON o.id = v.option_id
                WHERE v.user_id = :user_id
                ORDER BY p.id, v.voted_at DESC
            ) AS subquery
            ORDER BY voted_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM (
            SELECT DISTINCT p.id
            FROM polls p
            JOIN options o ON p.id = o.poll_id
            JOIN votes v ON o.id = v.option_id
            WHERE v.user_id = :user_id
        ) AS distinct_polls
        """, nativeQuery = true)
    Page<Poll> findVotedPolls(@Param("user_id") UUID userId, Pageable pageable);

}
