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
        String timeLeft,
        List<OptionResponseDTO> options,
        int totalVotes,
        String createdBy,
        String visibility,
        UUID userId,
        Long userVoteOptionId,
        boolean userAlreadyVoted,
        boolean isExpired,
        List<Long> winnerOptionIds
        ) {

    public PollResponseDTO{
        totalVotes = options.stream().mapToInt(OptionResponseDTO::votes).sum();
    }

    public static PollResponseDTO withoutVoteInfo(UUID id, String question, Instant createdAt,
                                                  Instant expiresAt, String timeLeft,
                                                  List<OptionResponseDTO> options, String createdBy, String visibility, UUID userId, boolean isExpired, List<Long> winnerOptionIds) {
        return new PollResponseDTO(id, question, createdAt, expiresAt, timeLeft,
                options, 0, createdBy, visibility, userId, null, false, isExpired, winnerOptionIds);
    }

    public static PollResponseDTO withVoteInfo(UUID id, String question, Instant createdAt,
                                               Instant expiresAt, String timeLeft,
                                               List<OptionResponseDTO> options, String createdBy, String visibility, UUID userId,
                                               Long userVoteOptionId, boolean isExpired, List<Long> winnerOptionIds) {
        boolean userAlreadyVoted = userVoteOptionId != null;
        return new PollResponseDTO(id, question, createdAt, expiresAt, timeLeft,
                options, 0, createdBy, visibility, userId, userVoteOptionId, userAlreadyVoted, isExpired, winnerOptionIds);
    }
}
