package com.project.enquete.core.enquete_platform.controller.dto.response;

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
        List<OptionResponseDTO> options) {
}
