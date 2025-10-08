package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.repository.projection.OptionStats;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {

//    @Query("SELECT p FROM Poll p LEFT JOIN FETCH p.options o LEFT JOIN FETCH o.votes WHERE p.id = :id")
//    Optional<Poll> findByIdWithOptionsAndVotes(@Param("id") UUID id);
//
//    @Query("SELECT p FROM Poll p LEFT JOIN FETCH p.options o LEFT JOIN FETCH o.votes")
//        List<Poll> findAllWithOptionsAndVotes();

//    @EntityGraph(attributePaths = {"options"})
    @Query("SELECT p FROM Poll p JOIN FETCH p.options WHERE p.createdBy.id = :userId")
    List<Poll> findAllWithOptionsForCurrentUser(@Param("userId") UUID userId);


    @Query("""
        SELECT p FROM Poll p
        JOIN FETCH p.createdBy u
        WHERE u.username = :username
        """)
    List<Poll> findPollsByUsername(@Param("username") String username);
}
