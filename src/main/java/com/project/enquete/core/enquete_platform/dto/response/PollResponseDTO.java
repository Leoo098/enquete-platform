package com.project.enquete.core.enquete_platform.dto.response;

import com.project.enquete.core.enquete_platform.model.User;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PollResponseDTO(
        UUID id,
        String question,
        Instant createdAt,
        Instant expiresAt,
        Duration timeLeft,
        List<OptionResponseDTO> options,
        int totalVotes,
        String createdBy,
        UUID userId,
        Long userVoteOptionId,
        boolean userAlreadyVoted) {

    public PollResponseDTO{
        totalVotes = options.stream().mapToInt(OptionResponseDTO::votes).sum();
    }

    public static PollResponseDTO withoutVoteInfo(UUID id, String question, Instant createdAt,
                                                  Instant expiresAt, Duration timeLeft,
                                                  List<OptionResponseDTO> options, String createdBy, UUID userId) {
        return new PollResponseDTO(id, question, createdAt, expiresAt, timeLeft,
                options, 0, createdBy, userId, null, false);
    }

    public static PollResponseDTO withVoteInfo(UUID id, String question, Instant createdAt,
                                               Instant expiresAt, Duration timeLeft,
                                               List<OptionResponseDTO> options, String createdBy, UUID userId,
                                               Long userVoteOptionId) {
        boolean userAlreadyVoted = userVoteOptionId != null;
        return new PollResponseDTO(id, question, createdAt, expiresAt, timeLeft,
                options, 0, createdBy, userId, userVoteOptionId, userAlreadyVoted);
    }
}
