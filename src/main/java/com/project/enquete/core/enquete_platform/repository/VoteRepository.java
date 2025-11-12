package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Vote;
import com.project.enquete.core.enquete_platform.repository.projection.VoteInfoProjection;
import com.project.enquete.core.enquete_platform.service.dto.PollVoteInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.option.id = :optionId")
    int countVotesByOptionId(@Param("optionId") Long optionId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.option.poll.id = :pollId")
    int countVotesByPollId(@Param("pollId") UUID pollId);

    @Query("SELECT COUNT(v) > 0 FROM Vote v WHERE v.user.id = :userId AND v.option.poll.id = :pollId")
    boolean existsByUserIdAndPollId(@Param("userId") UUID userId, @Param("pollId") UUID pollId);

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.option.poll.id = :pollId")
    Optional<Vote> findByUserIdAndOptionPollId(@Param("userId") UUID userId, @Param("pollId") UUID pollId);

    @Query("""
            SELECT v.option.id as optionId, COUNT(v) as voteCount
            FROM Vote v WHERE v.option.poll.id = :pollId
            GROUP BY v.option.id
            """)
    List<VoteInfoProjection> findVoteInfo(@Param("pollId") UUID pollId);

    @Query("""
        SELECT v.option.id FROM Vote v
        WHERE v.user.id = :userId AND v.option.poll.id = :pollId
        ORDER BY v.votedAt DESC
        LIMIT 1
        """)
    Long findVotedOptionByUserAndPoll(@Param("userId") UUID userId, @Param("pollId") UUID pollId);
}
