package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.repository.projection.OptionStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {

    @Query("""
    SELECT 
        o.id as id, 
        o.text as text, 
        COUNT(v) as voteCount,
        (COUNT(v) * 100.0 / NULLIF((
            SELECT COUNT(v2) FROM Vote v2 
            JOIN v2.option o2 
            WHERE o2.poll.id = :pollId
        ), 0)) as percentage
    FROM Option o LEFT JOIN o.votes v
    WHERE o.poll.id = :pollId
    GROUP BY o.id, o.text
    ORDER BY o.id
    """)
    List<OptionStats> getPollResults(@Param("pollId") UUID pollId);
}
