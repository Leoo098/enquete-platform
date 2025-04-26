package com.project.enquete.core.enquete_platform.controller.dto.response;

import com.project.enquete.core.enquete_platform.controller.dto.request.OptionDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PollResponseDTO(
        UUID id,
        String question,
        Instant createdAt,
        Instant expiresAt,
        List<OptionDTO> options) {
}
